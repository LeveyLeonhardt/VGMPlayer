package du.yufei.vgmplayer.ConfigDatabase;

/**
 * Created by edwar on 5/1/2017.
 */

public class Config {
    private String mJson;
    private int mLast;
    private boolean mSound;

    public Config(String json, int last, int sound){
        mJson = json;
        mLast = last;
        if(sound == 0){
            mSound = false;
        }else{
            mSound = true;
        }
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
