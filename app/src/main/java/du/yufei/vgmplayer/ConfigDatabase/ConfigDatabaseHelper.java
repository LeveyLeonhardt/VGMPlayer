package du.yufei.vgmplayer.ConfigDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by edwar on 5/1/2017.
 */

public class ConfigDatabaseHelper extends SQLiteOpenHelper {

    public ConfigDatabaseHelper(Context context){
        super(context, ConfigDbSchema.DATABASE_NAME, null, ConfigDbSchema.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ ConfigDbSchema.ConfigTable.NAME + "(" +
                ConfigDbSchema.ConfigTable.Cols.JSON+", "+
                ConfigDbSchema.ConfigTable.Cols.LASTPLAY+" integer, "+
                ConfigDbSchema.ConfigTable.Cols.SOUNDEFFECT+" integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
