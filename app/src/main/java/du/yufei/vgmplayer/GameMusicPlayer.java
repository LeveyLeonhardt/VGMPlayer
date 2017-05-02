package du.yufei.vgmplayer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by edwar on 4/30/2017.
 */

public class GameMusicPlayer {
    //mCurrentPlayer: The current activer player
    //mPlayer1: The player in charge of the first section and the looping section after mPlayer2 plays
    //mPlayer2: The player in charge of the looping section
    private MediaPlayer mCurrentPlayer, mPlayer1, mPlayer2;
    private Context mContext;
    private String mFile1, mFile2;
    private int mMusicId;

    public GameMusicPlayer(Context context, String file1, String file2, int id){
        //Initialize
        mContext = context;
        mFile1 = file1;
        mFile2 = file2;
        mPlayer1 = new MediaPlayer();
        mPlayer2 = new MediaPlayer();
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
            mPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
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
            mPlayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
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
                mCurrentPlayer.start();
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
        mCurrentPlayer.start();
    }

    //Pause music
    public void pause(){
        mCurrentPlayer.pause();
    }

    //Auto start
    public void autoStart(){
        mPlayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
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
    }

    //If the player is playing or not
    public boolean isPlaying(){
        return mCurrentPlayer.isPlaying();
    }

    //Get current position of the music
    public int getCurrentPosition(){
        return mCurrentPlayer.getCurrentPosition();
    }
}
