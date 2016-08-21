package net.cassiolandim.florianopolisroutes.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.cassiolandim.florianopolisroutes.R;
import net.cassiolandim.florianopolisroutes.fragment.DeparturesListFragment;
import net.cassiolandim.florianopolisroutes.fragment.StopsListFragment;

public class DetailsActivity extends ParentActivity {

    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRAS_NAME = "EXTRAS_NAME";

    private long routeId;

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routeId = getIntent().getLongExtra(EXTRA_ID, 0);
        if (routeId == 0) {
            finish();
            return;
        }

        String routeName = getIntent().getStringExtra(EXTRAS_NAME);

        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(routeName);
        ab.setDisplayHomeAsUpEnabled(true);

        String[] titles = {getString(R.string.tab_title_departures),
                getString(R.string.tab_title_stops)};

        this.mViewPager = (ViewPager) findViewById(R.id.view_pager);
        this.mAdapter = new MainFragmentPageAdapter(getSupportFragmentManager(), titles);

        mViewPager.setAdapter(mAdapter);
    }

    private class MainFragmentPageAdapter extends FragmentPagerAdapter {

        private String[] titles;

        public MainFragmentPageAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            this.titles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if (position == 0) {
                fragment = DeparturesListFragment.newInstance(routeId);
            } else {
                fragment = StopsListFragment.newInstance(routeId);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
