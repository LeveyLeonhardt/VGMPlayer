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
    private int mMusicId;

    public GameMusicPlayer(Context context, String file1, String file2, int id){
        //Initialize
        mPlayer1 = new MediaPlayer();
        mPlayer2 = new MediaPlayer();
        mMusicId = id;
        try {
            final Context fContext = context;
            final String fUrl1 = file1, fUrl2 = file2;
            //Set Data Source for both players
            mPlayer1.setDataSource(context.getExternalCacheDir().getPath()+file1);
            mPlayer2.setDataSource(context.getExternalCacheDir().getPath()+file2);
            //mPlayer2.setDataSource(afd2.getFileDescriptor(),afd2.getStartOffset(),afd2.getLength());
            //Set mPlayer1's OnCompletionListener so that it starts mPlayer2 automatically
            //and reset mPlayer 1 to the same looping part
            //Also set mPlayer2's next media player
            mPlayer1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        mCurrentPlayer = mPlayer2;
                        mPlayer1.reset();
                        mPlayer1.setDataSource(fContext.getExternalCacheDir().getPath()+fUrl2);
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
                        mCurrentPlayer = mPlayer1;
                        mPlayer2.reset();
                        mPlayer2.setDataSource(fContext.getExternalCacheDir().getPath()+fUrl2);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Start music
    public void start(){
        mCurrentPlayer.start();
    }

    //Pause music
    public void pause(){
        mCurrentPlayer.pause();
    }

    //Release resources for both players
    public void release(){
        mPlayer1.release();
        mPlayer2.release();
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
