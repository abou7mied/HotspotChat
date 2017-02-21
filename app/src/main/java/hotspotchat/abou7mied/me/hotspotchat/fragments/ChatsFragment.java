package hotspotchat.abou7mied.me.hotspotchat.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import hotspotchat.abou7mied.me.hotspotchat.BR;
import hotspotchat.abou7mied.me.hotspotchat.R;
import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.database.DBController;
import hotspotchat.abou7mied.me.hotspotchat.databinding.ChatsFragmentBinding;
import hotspotchat.abou7mied.me.hotspotchat.databinding.MessageItemContentBinding;
import hotspotchat.abou7mied.me.hotspotchat.messaging.Conversation;
import hotspotchat.abou7mied.me.hotspotchat.messaging.Message;
import hotspotchat.abou7mied.me.hotspotchat.net.Websocket;
import hotspotchat.abou7mied.me.hotspotchat.net.WsMessage;

/**
 * Created by abou7mied on 12/2/16.
 */

public class ChatsFragment extends Fragment {


    static ChatsFragment instance;
    public RecyclerView recyclerView;
    private SimpleItemRecyclerViewAdapter adapter;
    public static ArrayList<Message> messages = new ArrayList<>();
    ChatsFragmentBinding chatsFragmentBinding;
    public static boolean active;

    public static ChatsFragment getInstance() {
        return getInstance(true);
    }

    public static ChatsFragment getInstance(boolean createIfNotExists) {
        if (instance == null) {
            Bundle args = new Bundle();
            ChatsFragment fragment = new ChatsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        chatsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.chats_fragment, container, false);
        View root = chatsFragmentBinding.getRoot();

        recyclerView = chatsFragmentBinding.messages;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SimpleItemRecyclerViewAdapter(messages);
        recyclerView.setAdapter(adapter);

        chatsFragmentBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Websocket.getClient().isOpen()) {
                    Toast.makeText(getActivity(), "Not connected", Toast.LENGTH_SHORT).show();
                    return;
                }


                EditText editText = chatsFragmentBinding.message;
                String text = editText.getText().toString().trim();
                if (text.length() > 0) {
                    WsMessage wsMessage = new WsMessage(WsMessage.ACTION_SEND_MESSAGE);

                    Message message = new Message();
                    message.setId(System.currentTimeMillis());
                    message.setAuthor(App.getInstance().getMyProfile().getId());
                    message.setConverId(Conversation.GENERAL_CONVERSATION_ID);
                    message.setText(text);

                    wsMessage.putData(Conversation.ID_KEY, Conversation.GENERAL_CONVERSATION_ID);
                    wsMessage.putData(wsMessage.DATA_MESSAGE, new Gson().toJson(message));
                    Websocket.getClient().send(wsMessage);
                    editText.setText("");
                }
            }
        });

        return root;
    }

    public static void cacheMessage(Message message, boolean intoDb) {
        if (intoDb)
            Message.cacheMessageIntoDb(message);

        if (messages.indexOf(message.getId()) == -1)
            messages.add(message);

    }

    public static void loadMessages() {
        JSONArray messages = App.getInstance().getDbController().get(DBController.DB_TABLE_MESSAGES);
        try {
            for (int i = 0; i < messages.length(); i++) {
                Message message = new Gson().fromJson(messages.getString(i), Message.class);
                cacheMessage(message, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ArrayList<Message> mValues;

        public SimpleItemRecyclerViewAdapter(ArrayList<Message> items) {
            mValues = items;
        }

        @Override
        public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MessageItemContentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.message_item_content, parent, false);
            return new SimpleItemRecyclerViewAdapter.ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            Message message = mValues.get(position);
            holder.getBinding().setVariable(BR.message, message);
            holder.getBinding().executePendingBindings();

        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public MessageItemContentBinding binding;

            public ViewHolder(MessageItemContentBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public MessageItemContentBinding getBinding() {
                return binding;
            }
        }
    }

    public void notifyList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }


    public static boolean isActive() {
        return active && SampleFragmentPagerAdapter.getCurrentPosition() == 2;
    }
}
