package hotspotchat.abou7mied.me.hotspotchat.messaging;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.database.DBController;

/**
 * Created by abou7mied on 2/9/17.
 */

public class Message {


    public static final String ID_KEY = "id";
    public static final String CONVER_ID_KEY = "converId";
    public static final String AUTHOR_KEY = "author";
    public static final String TEXT_KEY = "text";

    private long id;
    private String converId;
    private String author;
    private String text;
    private String formatedTime;

    public Message() {

    }

    public Message(JSONObject data) {
        try {
            setId(data.getLong(ID_KEY));
            setAuthor(data.getString(AUTHOR_KEY));
            setText(data.getString(TEXT_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void setId(long id) {
        this.id = id;
    }

    public void setConverId(String converId) {
        this.converId = converId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public String getConverId() {
        return converId;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Profile getProfile() {
        return Profile.getProfile(getAuthor());
    }

    public static void cacheMessageIntoDb(Message message) {
        try {
            App.getInstance().getDbController().insertOrUpdate(DBController.DB_TABLE_MESSAGES, new JSONObject(new Gson().toJson(message)), Message.ID_KEY + "=? AND " + Message.CONVER_ID_KEY + "=?", new String[]{
                    message.getId() + "",
                    message.getConverId()
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String getFormatedTime() {
        if (formatedTime == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            formatedTime = dateFormat.format(getId());
        }
        return formatedTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Long) {
            if (this.getId() == (Long) obj) {
                return true;
            }
        }
        return super.equals(obj);
    }
}
