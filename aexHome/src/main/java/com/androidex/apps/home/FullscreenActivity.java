package com.androidex.apps.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidex.common.AndroidExActivityBase;
import com.androidex.common.DummyContent;
import com.androidex.common.LogFragment;
import com.androidex.common.OnMultClickListener;
import com.androidex.logger.Log;
import com.androidex.logger.LogWrapper;
import com.androidex.logger.MessageOnlyLogFilter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AndroidExActivityBase implements OnMultClickListener {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mContentView;
    private View mControlsView;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.aexsetting_main);
        hwservice.EnterFullScreen();
        getWindow().getDecorView().setBackgroundResource(R.drawable.default_wallpaper);
        initActionBar(R.id.toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mContentView = (ViewPager) findViewById(R.id.fullscreen_content);
        mContentView.setAdapter(mSectionsPagerAdapter);
        mContentView.setBackgroundResource(R.drawable.default_wallpaper);
        //registerMultClickListener(mContentView,this);

        mControlsView = findViewById(R.id.dummy_button);
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        mControlsView.setOnTouchListener(mDelayHideTouchListener);
        setFullScreenView(mContentView);
        setFullScreen(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(verify_password == 0)
        //    CheckPassword();
        hwservice.ExitFullScreen();
        EnableFullScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hwservice.ExitFullScreen();
        DisableFullScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitDialog();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hwservice.ExitFullScreen();
        DisableFullScreen();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
    }

    @Override
    public void initActionBar(int resId) {
        super.initActionBar(resId);
        Toolbar toolbar = (Toolbar) findViewById(resId);
        if (toolbar != null) {
            //toolbar.setLogo(com.androidex.aexapplibs.R.drawable.androidex);      //设置logo图片
            //toolbar.setNavigationIcon(com.androidex.aexapplibs.R.drawable.back);     //设置导航按钮
            toolbar.setTitle(R.string.app_name);          //设置标题
            toolbar.setSubtitle(R.string.app_subtitle);   //设置子标题
        }
    }

    @Override
    public void ShowControlBar() {
        mControlsView.setVisibility(View.VISIBLE);
        super.ShowControlBar();
    }

    @Override
    public void HideControlBar() {
        mControlsView.setVisibility(View.GONE);
        super.HideControlBar();
    }

    /** Create a chain of targets that will receive log data */
    @Override
    public void initializeLogging() {
        super.initializeLogging();

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager().findFragmentByTag("Log");
        if(logFragment != null) {
            LogWrapper logWrapper = (LogWrapper) Log.getLogNode();
            MessageOnlyLogFilter msgFilter = (MessageOnlyLogFilter)logWrapper.getNext();
            msgFilter.setNext(logFragment.getLogView());
        }
        Log.i(TAG, getString(R.string.ready));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem m4 = menu.add(R.string.str_quit);
        m4.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                showExitDialog();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public static final int DLG_NETINFO  = 1004;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
        case R.id.action_settings:
            {
                ViewGroup v = (ViewGroup)mContentView.getChildAt(mContentView.getCurrentItem());
                Intent mIntent = new Intent();
                mIntent.setAction(Intent.ACTION_VIEW);
                mIntent.setClassName("com.android.settings", "com.android.settings.Settings");
                mIntent.putExtra("back",true);
                sendBroadcast(new Intent("com.android.action.display_navigationbar"));
                startActivityForResult(mIntent,DLG_NETINFO);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean OnMultClick(View v, int times) {
        if(times == 4 && v.equals(mContentView)){     //连续4次点击执行事件
            //ToggleControlBar();
            Intent intent = new Intent(FullscreenActivity.ActionControlBar);
            intent.putExtra("flag","toggle");
            intent.putExtra("bar",true);
            mContentView.getContext().sendBroadcast(intent);
            return true;
        }
        return false;
    }

    static {
        DummyContent.addItem(new DummyContent.DummyItem("log","日志","", LogFragment.class,"url=log",true,0));
    };

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Fullscreen", "mContentView click");
                    Intent intent = new Intent(FullscreenActivity.ActionControlBar);
                    intent.putExtra("flag","toggle");
                    intent.putExtra("bar",true);
                    rootView.getContext().sendBroadcast(intent);
                }
            });
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    rootView.setBackgroundResource(R.drawable.default_wallpaper);
                    break;
                case 2:
                    rootView.setBackgroundResource(R.drawable.wallpaper01);
                    break;
                case 3:
                default:
                    rootView.setBackgroundResource(R.drawable.wallpaper02);
                    break;
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Main";
                case 1:
                    return "Setting";
                case 2:
                    return "Log";
            }
            return null;
        }
    }
}
