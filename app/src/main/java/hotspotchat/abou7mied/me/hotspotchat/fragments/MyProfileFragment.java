package hotspotchat.abou7mied.me.hotspotchat.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;

import hotspotchat.abou7mied.me.hotspotchat.AvatarsDialogInterface;
import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.activities.MyProfileActivity;
import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.core.MyProfile;
import hotspotchat.abou7mied.me.hotspotchat.core.Preferences;
import hotspotchat.abou7mied.me.hotspotchat.databinding.ProfileFragmentBinding;
import hotspotchat.abou7mied.me.hotspotchat.databinding.StartNetworkDialogBinding;
import hotspotchat.abou7mied.me.hotspotchat.net.NetWorkManager;


/**
 * Created by abou7mied on 12/2/16.
 */

public class MyProfileFragment extends Fragment {


    private static MyProfileFragment instance;
    AlertDialog dialog;
    ProgressDialog loadingDialog;


    public static MyProfileFragment getInstance() {
        if (instance == null) {
            Bundle args = new Bundle();
            MyProfileFragment fragment = new MyProfileFragment();
            fragment.setArguments(args);
            instance = fragment;
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ProfileFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false);

        final App app = (App) getActivity().getApplication();

        binding.setProfile(app.getMyProfile());
        binding.setPreferences(app.getPreferences());
        binding.setNetWorkManager(app.getNetWorkManager());

        final View view = binding.getRoot();


        StartNetworkDialogBinding startNetworkDialogBinding = DataBindingUtil.inflate(inflater, R.layout.start_network_dialog, null, false);
        startNetworkDialogBinding.setPreferences(app.getPreferences());

        dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Start network")
                .setView(startNetworkDialogBinding.getRoot())
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Preferences preferences = app.getPreferences();
                        String name = preferences.getNetworkName();
                        String pass = preferences.getNetworkPassword();

                        if (name.length() > 1 && pass.length() >= 8) {
                            loadingDialog = ProgressDialog.show(getActivity(), "",
                                    "Please wait...", true);

                            NetWorkManager.startHotspot(getActivity(), new NetWorkManager.Callback() {
                                @Override
                                public void done() {
                                    loadingDialog.dismiss();
                                    dialog.dismiss();
                                }
                            });
                        }


                    }
                });
            }
        });


        binding.notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ((App) getActivity().getApplication()).getPreferences().setNotificationsEnabled(b);

//                Snackbar.make(view, "Notifications " + (b ? "enabled" : "disabled"), Snackbar.LENGTH_SHORT)
//                        .show();

            }
        });

        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AvatarsDialogInterface) getActivity()).setAvatarsDialogVisibility(View.VISIBLE);
            }
        });

        binding.startNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        binding.stopNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetWorkManager.stopHotspot(getActivity());

            }
        });


        return view;
    }


}
