package hotspotchat.abou7mied.me.hotspotchat.fragments;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hotspotchat.abou7mied.me.hotspotchat.BR;
import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.databinding.OnlineFragmentBinding;
import hotspotchat.abou7mied.me.hotspotchat.databinding.ProfileItemContentBinding;
import hotspotchat.abou7mied.me.hotspotchat.net.client.WsClient;

/**
 * Created by abou7mied on 12/2/16.
 */

public class OnlineFragment extends Fragment {


    public static OnlineFragment newInstance() {
        Bundle args = new Bundle();
        OnlineFragment fragment = new OnlineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        OnlineFragmentBinding onlineFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.online_fragment, container, false);


        onlineFragmentBinding.itemList.setLayoutManager(new LinearLayoutManager(getActivity()));
        onlineFragmentBinding.itemList.setAdapter(new SimpleItemRecyclerViewAdapter(WsClient.profiles));


        return onlineFragmentBinding.getRoot();
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ObservableArrayList<Profile> mValues;

        public SimpleItemRecyclerViewAdapter(ObservableArrayList<Profile> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ProfileItemContentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.profile_item_content, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            final Profile item = mValues.get(position);
            holder.getBinding().setVariable(BR.profile, item);
            holder.getBinding().executePendingBindings();


//
//            ProfileItemContentBinding viewDataBinding = holder.getBinding();
////            viewDataBinding.setVariable(BR.profile, mValues.get(position));
//            viewDataBinding.setVariable(itemBinder.getBindingVariable(item), item);
//
//            viewDataBinding.executePendingBindings();

//            holder.profile = mValues.get(position);
//            holder.name.setText(mValues.get(position).getName());
//            holder.mContentView.setText(mValues.get(position).content);
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, ItemDetailActivity.class);
//                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.profile.id);
//
//                    context.startActivity(intent);
//                }
//            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public ImageView avatar;
            public TextView name;
            public Profile profile;
            public ProfileItemContentBinding binding;

            public ViewHolder(ProfileItemContentBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                View view = binding.getRoot();
                mView = view;
                avatar = (ImageView) view.findViewById(R.id.avatar);
                name = (TextView) view.findViewById(R.id.name);
            }

            public ProfileItemContentBinding getBinding() {
                return binding;
            }
        }
    }


}
