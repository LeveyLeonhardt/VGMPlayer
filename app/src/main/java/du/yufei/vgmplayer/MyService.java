package du.yufei.vgmplayer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private final String TAG = "MyService";

    private GameMusicPlayer mPlayer;
    private final IBinder mBinder = new LocalBinder();
    private int mId;

    public MyService() {
    }

    //Get the current music player
    public GameMusicPlayer getPlayer(){
        return mPlayer;
    }

    //Change song
    public void updatePlayer(int id, String file1, String file2){
        Log.d(TAG, "Update Player: Orig: " + mId + " New: "+id);
        if(mId != id) {
            //mPlayer.release();
            mId = id;
            mPlayer.setDataSource(file1,file2,id);
            mPlayer.prepare(true);
        }
    }

    //Return if music is currently playing
    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mId = intent.getIntExtra(MainActivity.ID,0);
        String file1 = intent.getStringExtra(MainActivity.FILENAME1);
        String file2 = intent.getStringExtra(MainActivity.FILENAME2);
        mPlayer = new GameMusicPlayer(getApplicationContext(),file1, file2,mId);
        mPlayer.prepare(false);
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }
}
