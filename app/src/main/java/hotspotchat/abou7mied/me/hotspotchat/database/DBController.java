package hotspotchat.abou7mied.me.hotspotchat.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import hotspotchat.abou7mied.me.hotspotchat.core.Profile;
import hotspotchat.abou7mied.me.hotspotchat.messaging.Conversation;
import hotspotchat.abou7mied.me.hotspotchat.messaging.Message;

public class DBController extends SQLiteOpenHelper {
    private static String DB_NAME = "app.db";
    private static int DB_VERSION = 6;

    public static String DB_TABLE_PROFILES = "profiles";
    public static String DB_TABLE_CONVERSATIONS = "conversations";
    public static String DB_TABLE_MESSAGES = "messages";


    private SQLiteDatabase DataB;
    private Context mContext;


    public DBController(Context applicationcontext) {
        super(applicationcontext, DB_NAME, null, DB_VERSION);
        Log.e("DBController", "DBController");
        mContext = applicationcontext;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        Log.e("SQLITE", "onCreate");

        String query;
        query = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_PROFILES + " ("
                + Profile.PROFILE_ID_KEY + " TEXT primary key, "
                + Profile.PROFILE_NAME_KEY + " TEXT, "
                + Profile.PROFILE_AVATAR_KEY + " integer DEFAULT 0"
                + ")";

        database.execSQL(query);

        query = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_CONVERSATIONS + " ("
                + Conversation.ID_KEY + " TEXT primary key, "
                + Conversation.LAST_MESSAGE_KEY + " TEXT"
                + ")";

        database.execSQL(query);


        query = "CREATE TABLE IF NOT EXISTS " + DB_TABLE_MESSAGES + " ("
                + Message.ID_KEY + " integer DEFAULT 0, "
                + Message.CONVER_ID_KEY + " TEXT, "
                + Message.AUTHOR_KEY + " TEXT, "
                + Message.TEXT_KEY + " TEXT, "
                + "primary key ("
                + Message.ID_KEY + ", "
                + Message.CONVER_ID_KEY
                + "))";

        database.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {

        Log.e("SQLITE", "onUpgrade, version_old: " + version_old + ", current_version: " + current_version + " ----");

        String query;

        query = "DROP TABLE IF EXISTS " + DB_TABLE_PROFILES;
        database.execSQL(query);

        query = "DROP TABLE IF EXISTS " + DB_TABLE_CONVERSATIONS;
        database.execSQL(query);

        query = "DROP TABLE IF EXISTS " + DB_TABLE_MESSAGES;
        database.execSQL(query);

        onCreate(database);
    }


    public long insertOrUpdate(String table, JSONObject queryValues, String where, String[] whereArgs) {
        return insertOrUpdate(table, queryValues, where, whereArgs, true);
    }

    public long insertOrUpdate(String table, JSONObject queryValues, String where, String[] whereArgs, boolean UPDATE) {
        SQLiteDatabase database = this.getWritableDatabase();
        int checkExits = where == null || whereArgs == null ? 0 : getCount(table, where, whereArgs);
        ContentValues queryValuesCV = jsonObjectToContentValuesChecker(queryValues);
        long i = -1;

        if (checkExits < 1)
            i = database.insert(table, null, queryValuesCV);
        else {
            if (UPDATE)
                database.update(table, queryValuesCV, where, whereArgs);
        }
        return i;

    }

    public int getCount(String table, String where, String[] whereArgs) {
        SQLiteDatabase database = this.getWritableDatabase();
        // where.split("=")[0].replace("!", "")
        Cursor dataCount = database.rawQuery("select rowid from " + table + " WHERE " + where + "", whereArgs);
        int count = dataCount.getCount();
        dataCount.close();
        return count;
    }

    public static ContentValues jsonObjectToContentValuesChecker(
            JSONObject data) {
        ContentValues newData = new ContentValues();
        try {
            Iterator<String> iter = data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                String value = data.get(key).toString();
                newData.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newData;
    }


    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db;
        if (DataB != null) {
            db = DataB;
        } else
            db = super.getWritableDatabase();
        return db;
    }


    public JSONArray get(String table) {
        return get(table, null, null, null, null, null, null);
    }

    public JSONArray get(String table, String[] columns, String where, String[] whereArgs, String orderBy,
                         String groupBy, String limit) {
        return get(table, columns, where, whereArgs, orderBy, groupBy, limit, null, false);
    }

    public JSONArray get(String table, String[] columns, String where, String[] whereArgs, String orderBy,
                         String groupBy, String limit, SQLiteDatabase database) {
        return get(table, columns, where, whereArgs, orderBy, groupBy, limit, null, false);
    }

    public JSONArray get(String table, String[] columns, String where, String[] whereArgs, String orderBy,
                         String groupBy, String limit, SQLiteDatabase database, boolean reverse) {
        JSONArray rows = new JSONArray();

        if (database == null)
            database = this.getWritableDatabase();

        Cursor cursor = null;
        try {
            cursor = database.query(table, columns, where, whereArgs, groupBy, null, orderBy, limit);

            if (reverse ? cursor.moveToLast() : cursor.moveToFirst()) {
                do {
                    JSONObject row = new JSONObject();
                    int AllColumns = cursor.getColumnCount();
                    for (int i = 0; i < AllColumns; i++) {
                        String value = cursor.isNull(i) ? "" : cursor.getString(i);
                        row.put(cursor.getColumnName(i), value);
                    }
                    rows.put(row);
                } while (reverse ? cursor.moveToPrevious() : cursor.moveToNext());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return rows;
    }





}
