package du.yufei.vgmplayer.ConfigDatabase;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by edwar on 5/1/2017.
 */

public class ConfigCursorWrapper extends CursorWrapper {

    public ConfigCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Config getConfig(){
        return new Config(getString(getColumnIndex(ConfigDbSchema.ConfigTable.Cols.HOST)),
                getString(getColumnIndex(ConfigDbSchema.ConfigTable.Cols.JSON)),
                getInt(getColumnIndex(ConfigDbSchema.ConfigTable.Cols.LASTPLAY)),
                getInt(getColumnIndex(ConfigDbSchema.ConfigTable.Cols.SOUNDEFFECT)));
    }
}
