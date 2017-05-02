package du.yufei.vgmplayer.ConfigDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by edwar on 5/1/2017.
 */

public class ConfigDatabase {
    private final Context mContext;
    private final SQLiteDatabase mDatabase;

    public ConfigDatabase(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new ConfigDatabaseHelper(mContext).getWritableDatabase();
    }

    public boolean hasConfig(){
        return queryConfig(null,null).getCount() != 0;
    }

    public Config getConfig(){
        ConfigCursorWrapper wrapper = queryConfig(null,null);
        Config config = null;
        try{
            wrapper.moveToFirst();
            config = wrapper.getConfig();
        }finally{
            wrapper.close();
        }
        return config;
    }

    public void add(Config config){
        ContentValues values = getContentValues(config);
        mDatabase.insert(ConfigDbSchema.ConfigTable.NAME,null,values);
    }

    public void update(Config config){
        ContentValues values = getContentValues(config);
        mDatabase.update(ConfigDbSchema.ConfigTable.NAME,values,null,null);
    }

    public void reset(){
        mDatabase.delete(ConfigDbSchema.ConfigTable.NAME,null,null);
    }

    private static ContentValues getContentValues(Config config){
        ContentValues values = new ContentValues();
        values.put(ConfigDbSchema.ConfigTable.Cols.JSON, config.getJson());
        values.put(ConfigDbSchema.ConfigTable.Cols.LASTPLAY, config.getLast());
        int se = 0;
        if(config.getSoundEnabled()){
            se = 1;
        }
        values.put(ConfigDbSchema.ConfigTable.Cols.SOUNDEFFECT, se);
        return values;
    }

    private ConfigCursorWrapper queryConfig(String where, String[] args){
        Cursor cursor = mDatabase.query(
                ConfigDbSchema.ConfigTable.NAME,
                null,
                where,
                args,
                null,
                null,
                null
        );
        return new ConfigCursorWrapper(cursor);
    }
}
