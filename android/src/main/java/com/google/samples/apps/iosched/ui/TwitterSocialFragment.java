/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.iosched.ui;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.samples.apps.iosched.Config;
import com.google.samples.apps.iosched.R;
import com.google.samples.apps.iosched.social.twitter.Tweet;
import com.google.samples.apps.iosched.social.twitter.TweetFetcher;
import com.google.samples.apps.iosched.util.ImageLoader;
import com.google.samples.apps.iosched.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TwitterSocialFragment extends ListFragment implements
        AbsListView.OnScrollListener,
        LoaderManager.LoaderCallbacks<List<Tweet>> {

    public static final String EXTRA_ADD_VERTICAL_MARGINS
            = "com.conferenceengineer.android.iosched.extra.ADD_VERTICAL_MARGINS";

    private static final String STATE_POSITION = "position";
    private static final String STATE_TOP = "top";

    private static final int STREAM_LOADER_ID = 0;

    private static final String TWITTER_PACKAGE_NAME = "com.twitter.android";

    private ImageLoader mImageLoader;

    private List<Tweet> mStream = new ArrayList<Tweet>();
    private StreamAdapter mStreamAdapter = new StreamAdapter();
    private int mListViewStatePosition;
    private int mListViewStateTop;

    public static TwitterSocialFragment newInstance() {
        return new TwitterSocialFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mListViewStatePosition = savedInstanceState.getInt(STATE_POSITION, -1);
            mListViewStateTop = savedInstanceState.getInt(STATE_TOP, 0);
        } else {
            mListViewStatePosition = -1;
            mListViewStateTop = 0;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.empty_twitter_stream));

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter
        // in the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(STREAM_LOADER_ID, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView lv = getListView();
        /*if (!UIUtils.isTablet(getActivity())) {
            view.setBackgroundColor(getResources().getColor(R.color.stream_spacer_color));
        }*/

        // Add some padding if the parent layout is too wide to avoid stretching the items too much
        // emulating the activity_letterboxed_when_large layout behaviour
        if (getArguments() != null && getArguments().getBoolean(EXTRA_ADD_VERTICAL_MARGINS, false)) {

            int verticalMargin = getResources().getDimensionPixelSize(R.dimen.twitter_stream_padding_vertical);
            if (verticalMargin > 0) {
                lv.setClipToPadding(false);
                lv.setPadding(0, verticalMargin, 0, verticalMargin);
            }
        }

        lv.setOnScrollListener(this);
        lv.setDrawSelectorOnTop(true);
        lv.setDivider(getResources().getDrawable(R.drawable.twitter_stream_list_divider));
        lv.setDividerHeight(getResources().getDimensionPixelSize(R.dimen.twitter_stream_divider_height));
        //view.setBackgroundColor(getResources().getColor(R.color.stream_list_bg_color));

        setListAdapter(mStreamAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.twitter_stream, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_compose:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Config.CONFERENCE_HASHTAG + "\n\n");

                UIUtils.preferPackageForIntent(getActivity(), intent, TWITTER_PACKAGE_NAME);

                startActivity(intent);

                return true;
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    }

    @Override
    public void onDestroyOptionsMenu() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (isAdded()) {
            View v = getListView().getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();
            outState.putInt(STATE_POSITION, getListView().getFirstVisiblePosition());
            outState.putInt(STATE_TOP, top);
        }
        super.onSaveInstanceState(outState);
    }

    public void refresh() {
        refresh(false);
    }

    public void refresh(boolean forceRefresh) {
        if (isStreamLoading() && !forceRefresh) {
            return;
        }

        // clear current items
        mStream.clear();
        mStreamAdapter.notifyDataSetInvalidated();

        if (isAdded()) {
            Loader loader = getLoaderManager().getLoader(STREAM_LOADER_ID);
            ((StreamLoader) loader).init();
        }

        loadMoreResults();
    }

    public void loadMoreResults() {
        if (isAdded()) {
            Loader loader = getLoaderManager().getLoader(STREAM_LOADER_ID);
            if (loader != null) {
                loader.forceLoad();
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Tweet activity = mStream.get(position);

        Intent postDetailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getUrlForTweet()));
        postDetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        UIUtils.preferPackageForIntent(getActivity(), postDetailIntent, TWITTER_PACKAGE_NAME);
        startActivity(postDetailIntent);
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (!isStreamLoading()
                && visibleItemCount != 0
                && firstVisibleItem + visibleItemCount >= totalItemCount - 1
                && streamHasMoreResults()) {
            loadMoreResults();
        }
    }

    @Override
    public Loader<List<Tweet>> onCreateLoader(int id, Bundle args) {
        return new StreamLoader(getActivity());

    }

    @Override
    public void onLoadFinished(Loader<List<Tweet>> listLoader, List<Tweet> activities) {
        if (activities != null) {
            mStream = activities;
        }
        mStreamAdapter.notifyDataSetChanged();
        if (mListViewStatePosition != -1 && isAdded()) {
            getListView().setSelectionFromTop(mListViewStatePosition, mListViewStateTop);
            mListViewStatePosition = -1;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Tweet>> listLoader) {
    }

    private boolean isStreamLoading() {
        if (isAdded()) {
            final Loader loader = getLoaderManager().getLoader(STREAM_LOADER_ID);
            if (loader != null) {
                return ((StreamLoader) loader).isLoading();
            }
        }
        return true;
    }

    private boolean streamHasMoreResults() {
        if (isAdded()) {
            final Loader loader = getLoaderManager().getLoader(STREAM_LOADER_ID);
            if (loader != null) {
                return ((StreamLoader) loader).hasMoreResults();
            }
        }
        return false;
    }

    private boolean streamHasError() {
        if (isAdded()) {
            final Loader loader = getLoaderManager().getLoader(STREAM_LOADER_ID);
            if (loader != null) {
                return ((StreamLoader) loader).hasError();
            }
        }
        return false;
    }

    private static class StreamLoader extends AsyncTaskLoader<List<Tweet>> {
        List<Tweet> mActivities;
        private String mNextPageToken;
        private boolean mIsLoading;
        private boolean mHasError;

        public StreamLoader(Context context) {
            super(context);
            init();
        }

        private void init() {
            mHasError = false;
            mNextPageToken = null;
            mIsLoading = true;
            mActivities = null;
        }

        @Override
        public List<Tweet> loadInBackground() {
            mIsLoading = true;

            try {
                return TweetFetcher.getInstance().fetchTweets();
            } catch (IOException e) {
                e.printStackTrace();
                mHasError = true;
            }
            return null;
        }

        @Override
        public void deliverResult(List<Tweet> activities) {
            mIsLoading = false;
            if (activities != null) {
                if (mActivities == null) {
                    mActivities = activities;
                } else {
                    mActivities.addAll(activities);
                }
            }
            if (isStarted()) {
                // Need to return new ArrayList for some reason or onLoadFinished() is not called
                super.deliverResult(mActivities == null ?
                        null : new ArrayList<Tweet>(mActivities));
            }
        }

        @Override
        protected void onStartLoading() {
            if (mActivities != null) {
                // If we already have results and are starting up, deliver what we already have.
                deliverResult(null);
            } else {
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            mIsLoading = false;
            cancelLoad();
        }

        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();
            mActivities = null;
        }

        public boolean isLoading() {
            return mIsLoading;
        }

        public boolean hasMoreResults() {
            return mNextPageToken != null;
        }

        public boolean hasError() {
            return mHasError;
        }

        public void refresh() {
            reset();
            startLoading();
        }
    }

    private class StreamAdapter extends BaseAdapter {
        private static final int VIEW_TYPE_ACTIVITY = 0;
        private static final int VIEW_TYPE_LOADING = 1;

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == VIEW_TYPE_ACTIVITY;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return mStream.size() + (
                    // show the status list row if...
                    ((isStreamLoading() && mStream.size() == 0) // ...this is the first load
                            || streamHasMoreResults() // ...or there's another page
                            || streamHasError()) // ...or there's an error
                            ? 1 : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return (position >= mStream.size())
                    ? VIEW_TYPE_LOADING
                    : VIEW_TYPE_ACTIVITY;
        }

        @Override
        public Object getItem(int position) {
            return (getItemViewType(position) == VIEW_TYPE_ACTIVITY)
                    ? mStream.get(position)
                    : null;
        }

        @Override
        public long getItemId(int position) {
            // TODO: better unique ID heuristic
            /*
            return (getItemViewType(position) == VIEW_TYPE_ACTIVITY)
                    ? mStream.get(position).getId().hashCode()
                    : -1;
            */
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (getItemViewType(position) == VIEW_TYPE_LOADING) {
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.list_item_twitter_status, parent, false);
                }

                if (streamHasError()) {
                    convertView.findViewById(android.R.id.progress).setVisibility(View.GONE);
                    ((TextView) convertView.findViewById(android.R.id.text1)).setText(
                            R.string.twitter_stream_error);
                } else {
                    convertView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(android.R.id.text1)).setText(
                            R.string.loading);
                }

                return convertView;

            } else {
                Tweet tweet = (Tweet) getItem(position);
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.list_item_twitter_activity, parent, false);
                }

                TextView textView = (TextView) convertView.findViewById(R.id.stream_user_name);
                textView.setText(tweet.name);
                textView.setCompoundDrawablesWithIntrinsicBounds(
                    tweet.verified ? R.drawable.ic_twitter_verified_badge : 0, 0, 0, 0);

                ((TextView)convertView.findViewById(R.id.stream_user_handle)).setText("@" + tweet.screenName);
                ((TextView)convertView.findViewById(R.id.stream_timestamp)).setText(
                    DateUtils.getRelativeTimeSpanString(tweet.createdAt));

                textView = (TextView)convertView.findViewById(R.id.stream_content);
                textView.setText(tweet.text);

                textView = (TextView)convertView.findViewById(R.id.stream_retweets);
                final Long retweetCount = tweet.retweetCount;
                if (retweetCount > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(getString(R.string.tweet_retweets, retweetCount));
                }
                else {
                    textView.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(tweet.profileImageURL) && mImageLoader != null) {
                    mImageLoader.loadImage(tweet.profileImageURL,
                                          (ImageView) convertView.findViewById(R.id.stream_user_pic));
                }

                return convertView;
            }
        }
    }
}
