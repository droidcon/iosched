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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import com.funkyandroid.droidcon.uk.iosched.R;
import com.funkyandroid.droidcon.uk.iosched.util.ImageLoader;
import com.funkyandroid.droidcon.uk.iosched.util.UIUtils;

public class SocialStreamActivity extends SimpleSinglePaneActivity implements ImageLoader.ImageLoaderProvider {

    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = new ImageLoader(this, R.drawable.person_image_empty)
            .setMaxImageSize(getResources().getDimensionPixelSize(R.dimen.stream_image_size))
            .setFadeInImage(UIUtils.hasHoneycombMR1());
    }

    @Override
    protected Fragment onCreatePane() {
        setIntent(getIntent().putExtra(SocialStreamFragment.EXTRA_ADD_VERTICAL_MARGINS, true));
        return new SocialStreamFragment();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_plus_stream;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra(SocialStreamFragment.EXTRA_QUERY));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.social_stream_standalone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                ((SocialStreamFragment) getFragment()).refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ImageLoader getImageLoaderInstance() {
        return mImageLoader;
    }
}
