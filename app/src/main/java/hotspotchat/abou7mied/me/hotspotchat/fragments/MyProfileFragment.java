package hotspotchat.abou7mied.me.hotspotchat.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.databinding.MyProfileFragmentBinding;


/**
 * Created by abou7mied on 12/2/16.
 */

public class MyProfileFragment extends Fragment {


    public static MyProfileFragment newInstance() {
        Bundle args = new Bundle();
        MyProfileFragment fragment = new MyProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        MyProfileFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.my_profile_fragment, container, false);

        App app = (App) getActivity().getApplication();

        binding.setProfile(app.getMyProfile());

        View view = binding.getRoot();

        return view;
    }


}
