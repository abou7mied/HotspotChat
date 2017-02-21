package hotspotchat.abou7mied.me.hotspotchat.messaging;

import java.util.ArrayList;
import java.util.HashMap;

import hotspotchat.abou7mied.me.hotspotchat.core.Profile;

/**
 * Created by abou7mied on 12/15/16.
 */

public class Conversation {


    public static final String ID_KEY = "id";
    public static final String LAST_MESSAGE_KEY = "last_message";
    public static final String GENERAL_CONVERSATION_ID = "general";

    public static HashMap<String, ArrayList<Message>> messages = new HashMap<>();

    String id, name;
    ArrayList<Profile> participants;


    public Conversation(ArrayList<Profile> participants) {
        // TODO: sort participants
        this.participants = participants;
        String id = "";
        if (participants != null) {
            for (Profile profile : participants) {
                if (!id.equals("")) {
                    id += ",";
                }

                id += profile.getId();
            }

        }

        setId(id);


    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setGeneral() {
        setId(GENERAL_CONVERSATION_ID);
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isGeneral() {
        return getId().equals(GENERAL_CONVERSATION_ID);
    }


    public Profile getPartnerProfile() {
//        Iterator<Map.Entry<String, Profile>> iterator = participants.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Profile> entry = iterator.next();
//            if (entry.getKey() == MyProfile.getInstance().getId()) {
//                return entry.getValue();
//            }
//        }
        return null;
    }


}
