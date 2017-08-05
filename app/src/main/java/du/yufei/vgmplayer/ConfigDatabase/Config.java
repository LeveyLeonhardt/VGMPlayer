package du.yufei.vgmplayer.ConfigDatabase;

/**
 * Created by edwar on 5/1/2017.
 */

public class Config {
    private String mJson, mHost;
    private int mLast;
    private boolean mSound;

    public Config(String host, String json, int last, int sound){
        mHost = host;
        mJson = json;
        mLast = last;
        if(sound == 0){
            mSound = false;
        }else{
            mSound = true;
        }
    }

    public String getHost(){
        return mHost;
    }

    public String getJson(){
        return mJson;
    }

    public int getLast(){
        return mLast;
    }

    public boolean getSoundEnabled(){
        return mSound;
    }

}
