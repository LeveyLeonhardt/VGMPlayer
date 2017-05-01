package du.yufei.vgmplayer;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Connection.MusicParsedListener{

    public static final String FILENAME1 = "PROJECT3.FILENAME1";
    public static final String FILENAME2 = "PROJECT3.FILENAME2";

    private static GameMusicPlayer mPlayer;
    private Connection mConnection;
    private String mMusicJson;
    private Music mCurrentMusic;
    private int mDownloadCounter, mFileNumber;

    private TextView mTrackName, mGameName, mArtistName;
    private ImageView mArtwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConnection = new Connection(this);
        mDownloadCounter = 0;
        mTrackName = (TextView)findViewById(R.id.text_track_name_main);
        mGameName = (TextView) findViewById(R.id.text_game_name_main);
        mArtistName = (TextView)findViewById(R.id.text_artist_name_main);
        mArtwork = (ImageView) findViewById(R.id.image_album_main);
    }

    public void toggle(View view){
        if(mPlayer.isPlaying()){
            String pos = String.valueOf(mPlayer.getCurrentPosition());
            //((TextView)findViewById(R.id.text_main)).setText(pos);
            mPlayer.pause();
        }else{
            mPlayer.start();
        }
    }

    public void downloadFiles(String[] filenames){
        mFileNumber = filenames.length;
        for(int i = 0; i < filenames.length; i++){
            mConnection.downloadFile(filenames[i]);
        }
    }

    @Override
    public void jsonParsed(String json)  {
        mMusicJson = json;
        Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
        try {
            JSONArray jsonArray = new JSONArray(json);
            JSONObject musArray = jsonArray.getJSONObject(2);
            mCurrentMusic = new Music(musArray.getInt("id"),musArray.getString("title")
                    ,musArray.getString("game"),musArray.getString("composer")
                    ,musArray.getString("file1"),musArray.getString("file2")
                    ,musArray.getString("artwork"));
            downloadFiles(mCurrentMusic.getDownloadFilenames());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void jsonFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.dialog_failed).setTitle(R.string.dialog_failed_title)
                        .setPositiveButton(R.string.dialog_failed_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).create().show();
            }
        });

    }

    @Override
    public void musicParsed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDownloadCounter++;
                if(mDownloadCounter == mFileNumber){
                    Toast.makeText(MainActivity.this,"DownloadComplete",Toast.LENGTH_SHORT).show();
                    mTrackName.setText(mCurrentMusic.getName());
                    mGameName.setText(mCurrentMusic.getGame());
                    mArtistName.setText(mCurrentMusic.getArtist());
                    mArtwork.setImageURI(Uri.parse(getExternalCacheDir()+mCurrentMusic.getImageFilename()));
                    //if(mPlayer != null) {
                        mPlayer = new GameMusicPlayer(MainActivity.this, mCurrentMusic.getFiles()[0], mCurrentMusic.getFiles()[1]);
                    //}
                }
            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mPlayer.release();
    }
}
