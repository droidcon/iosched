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

package com.funkyandroid.droidcon.uk.iosched.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.TypedValue;
import android.view.*;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TweetResponse;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.Tweets;
import com.funkyandroid.droidcon.uk.droidconsched.io.model.TweetsResponse;
import com.funkyandroid.droidcon.uk.iosched.Config;
import com.funkyandroid.droidcon.uk.iosched.R;
import com.funkyandroid.droidcon.uk.iosched.util.UIUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.LOGD;
import static com.funkyandroid.droidcon.uk.iosched.util.LogUtils.makeLogTag;

/**
 * A fragment that renders Google+ search results for a given query, provided as the
 * {@link SocialStreamFragment#EXTRA_QUERY} extra in the fragment arguments. If no
 * search query is provided, the conference hashtag is used as the default query.
 */
public class SocialStreamFragment extends ListFragment implements
        AbsListView.OnScrollListener,
        LoaderManager.LoaderCallbacks<List<TweetResponse>> {

    private static final String TAG = makeLogTag(SocialStreamFragment.class);

    public static final String EXTRA_QUERY = "com.funkyandroid.droidcon.uk.iosched.extra.QUERY";
    public static final String EXTRA_ADD_VERTICAL_MARGINS
            = "com.funkyandroid.droidcon.uk.iosched.extra.ADD_VERTICAL_MARGINS";

    private static final String STATE_POSITION = "position";
    private static final String STATE_TOP = "top";

    private static final int STREAM_LOADER_ID = 0;

    private String mSearchString;

    private List<TweetResponse> mStream = new ArrayList<TweetResponse>();
    private StreamAdapter mStreamAdapter = new StreamAdapter();
    private int mListViewStatePosition;
    private int mListViewStateTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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

        final View layoutRoot = super.onCreateView(inflater, container, savedInstanceState);
        ListView lv = (ListView) layoutRoot.findViewById(android.R.id.list);
        lv.setDivider(getResources().getDrawable(R.drawable.stream_list_separator));

        // Add some padding if the parent layout is too wide to avoid stretching the items too much
        // emulating the activity_letterboxed_when_large layout behaviour
        if (container.getWidth() >= getResources().getDimensionPixelSize(R.dimen.stream_max_width)) {
            container.setBackgroundResource(R.drawable.grey_background_pattern);

            lv.setBackgroundResource(R.drawable.grey_frame_on_white);

            final ViewGroup.LayoutParams lp = lv.getLayoutParams();
            lp.width = getResources().getDimensionPixelSize(R.dimen.stream_max_width);
            lv.setLayoutParams(lp);
            lv.requestLayout();
        }
        return layoutRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.empty_social_stream));

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter
        // in the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(STREAM_LOADER_ID, null, this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
        if (!UIUtils.isTablet(getActivity())) {
            view.setBackgroundColor(getResources().getColor(R.color.stream_spacer_color));
        }

        if (getArguments() != null
                && getArguments().getBoolean(EXTRA_ADD_VERTICAL_MARGINS, false)) {
            int verticalMargin = getResources().getDimensionPixelSize(
                    R.dimen.plus_stream_padding_vertical);
            if (verticalMargin > 0) {
                listView.setClipToPadding(false);
                listView.setPadding(0, verticalMargin, 0, verticalMargin);
            }
        }

        listView.setOnScrollListener(this);
        listView.setDrawSelectorOnTop(true);
        listView.setDivider(getResources().getDrawable(android.R.color.transparent));
        listView.setDividerHeight(getResources()
                .getDimensionPixelSize(R.dimen.page_margin_width));

        TypedValue v = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.activatableItemBackground, v, true);
        listView.setSelector(v.resourceId);

        setListAdapter(mStreamAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.social_stream, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_compose:
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(mSearchString + "\n\n")
                        .getIntent();

                UIUtils.preferPackageForIntent(getActivity(), intent,
                        UIUtils.TWITTER_PACKAGE_NAME);

                startActivity(intent);

                LOGD("Tracker", "Home Screen Dashboard: Click, post to g+");

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

    public void refresh(String newQuery) {
        mSearchString = newQuery;
        refresh(true);
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
        TweetResponse activity = mStream.get(position);

        Intent postDetailIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getUrl()));
        postDetailIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        UIUtils.preferPackageForIntent(getActivity(), postDetailIntent, UIUtils.TWITTER_PACKAGE_NAME);
        startActivity(postDetailIntent);
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
        // Pause disk cache access to ensure smoother scrolling
        /*
            TODO: Improve image loading
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            mImageLoader.stopProcessingQueue();
        } else {
            mImageLoader.startProcessingQueue();
        }
         */
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (!isStreamLoading()
                && streamHasMoreResults()
                && visibleItemCount != 0
                && firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
            loadMoreResults();
        }
    }

    @Override
    public Loader<List<TweetResponse>> onCreateLoader(int id, Bundle args) {
        return new StreamLoader(getActivity());

    }

    @Override
    public void onLoadFinished(Loader<List<TweetResponse>> listLoader, List<TweetResponse> activities) {
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
    public void onLoaderReset(Loader<List<TweetResponse>> listLoader) {
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

    private static class StreamLoader extends AsyncTaskLoader<List<TweetResponse>> {
        List<TweetResponse> mActivities;
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
        public List<TweetResponse> loadInBackground() {
            mIsLoading = true;

            try {
                TweetsResponse tweets = new Tweets().list(Config.EVENT_ID).execute();
                mHasError = false;
                return tweets.getTweets();
            } catch (IOException e) {
                e.printStackTrace();
                mHasError = true;
            }
            return null;
        }

        @Override
        public void deliverResult(List<TweetResponse> activities) {
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
                        null : new ArrayList<TweetResponse>(mActivities));
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

        public void setSearchString(String searchString) {
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
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == VIEW_TYPE_LOADING) {
                if (convertView == null) {
                    convertView = getLayoutInflater(null).inflate(
                            R.layout.list_item_stream_status, parent, false);
                }

                if (streamHasError()) {
                    convertView.findViewById(android.R.id.progress).setVisibility(View.GONE);
                    ((TextView) convertView.findViewById(android.R.id.text1)).setText(
                            R.string.stream_error);
                } else {
                    convertView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(android.R.id.text1)).setText(
                            R.string.loading);
                }

                return convertView;

            } else {
                TweetResponse activity = (TweetResponse) getItem(position);
                if (convertView == null) {
                    convertView = getLayoutInflater(null).inflate(
                            R.layout.list_item_stream_activity, parent, false);
                }

                ((TextView)convertView.findViewById(R.id.stream_user_name)).setText(activity.getName());

                TextView content = (TextView)convertView.findViewById(R.id.stream_content);
                content.setText(activity.getText());
                content.setVisibility(View.VISIBLE);

                return convertView;
            }
        }
    }
}
