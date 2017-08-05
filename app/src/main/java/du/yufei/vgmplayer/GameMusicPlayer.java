package du.yufei.vgmplayer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.standard.StandardMediaPlayerFactory;

import java.io.IOException;

/**
 * Created by edwar on 4/30/2017.
 */

public class GameMusicPlayer implements AudioManager.OnAudioFocusChangeListener{

    private final static String TAG = "VGMPLAYER.GAMEMUSICPLAYER";

    public final static String ISPLAYING = "VGMPLAYER.GAMEMUSICPLAYER.ISPLAYING";
    public final static String ACTION = "VGMPLAYER.GAMEMUSICPLAYER.ACTION";
    //mCurrentPlayer: The current activer player
    //mPlayer1: The player in charge of the first section and the looping section after mPlayer2 plays
    //mPlayer2: The player in charge of the looping section
    private IBasicMediaPlayer mCurrentPlayer, mPlayer1, mPlayer2;
    private Context mContext;
    private String mFile1, mFile2;
    private AudioManager mAudioManager;
    private int mMusicId;

    public GameMusicPlayer(Context context, String file1, String file2, int id){
        //Initialize
        mContext = context;
        mFile1 = file1;
        mFile2 = file2;
        StandardMediaPlayerFactory factory = new StandardMediaPlayerFactory(mContext);
        mPlayer1 = factory.createMediaPlayer();
        mPlayer2 = factory.createMediaPlayer();
        mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        mMusicId = id;
    }

    //Prepare MediaPlayers
    public void prepare(boolean autostart){
        try {
            //Set Data Source for both players
            mPlayer1.setDataSource(mContext.getExternalCacheDir().getPath()+mFile1);
            mPlayer2.setDataSource(mContext.getExternalCacheDir().getPath()+mFile2);
            //mPlayer2.setDataSource(afd2.getFileDescriptor(),afd2.getStartOffset(),afd2.getLength());
            //Set mPlayer1's OnCompletionListener so that it starts mPlayer2 automatically
            //and reset mPlayer 1 to the same looping part
            //Also set mPlayer2's next media player
            mPlayer1.setOnCompletionListener(new IBasicMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IBasicMediaPlayer mp) {
                    try {
                        mCurrentPlayer.reset();
                        mCurrentPlayer = mPlayer2;
                        mPlayer1.reset();
                        mPlayer1.setDataSource(mContext.getExternalCacheDir().getPath()+mFile2);
                        //mPlayer1.setDataSource(afd2.getFileDescriptor(),afd2.getStartOffset(),afd2.getLength());
                        mPlayer1.prepare();
                        mPlayer2.setNextMediaPlayer(mPlayer1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //Set mPlayer2's OnCompletionListener
            mPlayer2.setOnCompletionListener(new IBasicMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IBasicMediaPlayer mp) {
                    try {
                        mCurrentPlayer.reset();
                        mCurrentPlayer = mPlayer1;
                        mPlayer2.reset();
                        mPlayer2.setDataSource(mContext.getExternalCacheDir().getPath()+mFile2);
                        mPlayer2.prepare();
                        mPlayer1.setNextMediaPlayer(mPlayer2);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            //Prepare
            mPlayer1.prepare();
            mPlayer2.prepare();
            //Set them to loop each other
            mPlayer1.setNextMediaPlayer(mPlayer2);
            mPlayer2.setNextMediaPlayer(mPlayer1);
            //Set mPlayer1 as the current player
            mCurrentPlayer = mPlayer1;
            if(autostart){
                start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Change music
    public void setDataSource(String file1, String file2, int id){
        //mCurrentPlayer.release();
        //mCurrentPlayer.reset();
        mPlayer1.reset();
        mPlayer2.reset();
        mFile1 = file1;
        mFile2 = file2;
        mMusicId = id;
    }

    //Start music
    public void start(){
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
            mCurrentPlayer.start();
        else
            Toast.makeText(mContext, "Error: Cannot Get Audio Focus", Toast.LENGTH_SHORT).show();
    }

    //Pause music
    public void pause(){
        mAudioManager.abandonAudioFocus(this);
        mCurrentPlayer.pause();
    }

    //Auto start
    public void autoStart(){
        mPlayer1.setOnPreparedListener(new IBasicMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IBasicMediaPlayer mp) {
                mp.start();
            }
        });
    }

    //Release resources for both players
    public void release(){
        mPlayer1.release();
        mPlayer2.release();
        mPlayer1 = null;
        mPlayer2 = null;
        mCurrentPlayer = null;
    }

    //If the player is playing or not
    public boolean isPlaying(){
        return mCurrentPlayer.isPlaying();
    }

    //Get current position of the music
    public int getCurrentPosition(){
        return mCurrentPlayer.getCurrentPosition();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d("GameMusicPlayer", "AUDIOFOCUS_GAIN");
                if(mCurrentPlayer == null)
                    prepare(false);
                mCurrentPlayer.start();
                mCurrentPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d("GameMusicPlayer", "AUDIOFOCUS_LOSS");
                mCurrentPlayer.stop();
                release();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d("GameMusicPlayer", "AUDIOFOCUS_LOSS_TRANSIENT");
                mCurrentPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d("GameMusicPlayer", "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                mCurrentPlayer.setVolume(0.3f, 0.3f);
        }
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra(ISPLAYING,isPlaying());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
