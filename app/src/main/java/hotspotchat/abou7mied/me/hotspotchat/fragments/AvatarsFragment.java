package hotspotchat.abou7mied.me.hotspotchat.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.core.App;

/**
 * Created by abou7mied on 2/9/17.
 */

public class AvatarsFragment extends Fragment {
    public static final ArrayList<Integer> avatars = new ArrayList<>();

    static {
        avatars.add(R.drawable.avatar_1);
        avatars.add(R.drawable.avatar_2);
        avatars.add(R.drawable.avatar_3);
        avatars.add(R.drawable.avatar_4);
        avatars.add(R.drawable.avatar_5);
        avatars.add(R.drawable.avatar_6);
        avatars.add(R.drawable.avatar_7);
        avatars.add(R.drawable.avatar_8);
        avatars.add(R.drawable.avatar_9);
        avatars.add(R.drawable.avatar_10);
        avatars.add(R.drawable.avatar_11);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.avatars_fragment,
                container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.avatars);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(avatars));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.closeAvatars);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAvatarsDialogVisibility(View.GONE);
            }
        });

    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ArrayList<Integer> mValues;

        SimpleItemRecyclerViewAdapter(ArrayList<Integer> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.avatar_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int item = mValues.get(position);

            ImageLoader imageLoader = ImageLoader.getInstance();

            imageLoader.displayImage("drawable://" + item, holder.avatar);
            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((App) getActivity().getApplication()).getMyProfile().setAvatar(position + 1);
                    setAvatarsDialogVisibility(View.GONE);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ImageButton avatar;

            ViewHolder(View view) {
                super(view);
                avatar = (ImageButton) view.findViewById(R.id.avatar);
            }

        }
    }

    public void setAvatarsDialogVisibility(int visibility) {
        getView().findViewById(R.id.avatarDialog).setVisibility(visibility);
    }

}
