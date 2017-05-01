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

    public GameMusicPlayer getPlayer(){
        return mPlayer;
    }

    public void updatePlayer(int id, String file1, String file2){
        if(mId != id) {
            mPlayer.release();
            mId = id;
            mPlayer = new GameMusicPlayer(getApplicationContext(), file1, file2, mId, true);
        }
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
        mPlayer = new GameMusicPlayer(getApplicationContext(),file1, file2,mId, false);
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }
}
