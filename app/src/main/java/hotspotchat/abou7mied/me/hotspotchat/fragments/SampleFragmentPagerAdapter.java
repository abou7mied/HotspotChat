package hotspotchat.abou7mied.me.hotspotchat.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by abou7mied on 12/2/16.
 */

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[]{"Online", "Messages", "Profile"};
    final int PAGE_COUNT = tabTitles.length;
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 2)
            return MyProfileFragment.newInstance();
        return OnlineFragment.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
