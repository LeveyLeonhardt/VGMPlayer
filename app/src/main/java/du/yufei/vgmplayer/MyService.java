package du.yufei.vgmplayer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private GameMusicPlayer mPlayer;
    private final IBinder mBinder = new LocalBinder();

    public MyService() {
    }

    public GameMusicPlayer getPlayer(){
        return mPlayer;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String file1 = intent.getStringExtra(MainActivity.FILENAME1);
        String file2 = intent.getStringExtra(MainActivity.FILENAME2);
        mPlayer = new GameMusicPlayer(getApplicationContext(),file1, file2);
        return START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }
}
