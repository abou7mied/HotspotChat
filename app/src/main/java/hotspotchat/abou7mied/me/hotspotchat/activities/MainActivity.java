package hotspotchat.abou7mied.me.hotspotchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import hotspotchat.abou7mied.me.hotspotchat.AvatarsDialogInterface;
import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.fragments.AvatarsFragment;
import hotspotchat.abou7mied.me.hotspotchat.fragments.OnlineFragment;
import hotspotchat.abou7mied.me.hotspotchat.fragments.SampleFragmentPagerAdapter;

public class MainActivity extends AppCompatActivity implements AvatarsDialogInterface {

    private static MainActivity instance;
    private App app;
    private SampleFragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        app = (App) this.getApplication();

        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragmentPagerAdapter = new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                this);
        viewPager.setAdapter(fragmentPagerAdapter);
        if (!app.isProfilePrepared()) {
            Intent i = new Intent(this, MyProfileActivity.class);
            startActivity(i);
            viewPager.setCurrentItem(2);
        }else{
            viewPager.setCurrentItem(1);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void setAvatarsDialogVisibility(int visibility) {
        AvatarsFragment fragment = (AvatarsFragment) getFragmentManager()
                .findFragmentById(R.id.avatarsFragment);
        fragment.setAvatarsDialogVisibility(visibility);
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public OnlineFragment getOnlineFragment() {
        return (OnlineFragment) fragmentPagerAdapter.getItem(0);
    }
}
