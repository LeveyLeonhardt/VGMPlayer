package du.yufei.vgmplayer.ConfigDatabase;

/**
 * Created by edwar on 5/1/2017.
 */

public class ConfigDbSchema {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "config_database.db";

    public static final class ConfigTable{
        public static final String NAME = "Config";
        public static final class Cols{
            public static final String JSON = "json";
            public static final String LASTPLAY = "last";
            public static final String SOUNDEFFECT = "se";
        }
    }
}
