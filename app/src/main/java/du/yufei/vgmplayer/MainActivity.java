package du.yufei.vgmplayer;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import du.yufei.vgmplayer.ConfigDatabase.Config;
import du.yufei.vgmplayer.ConfigDatabase.ConfigDatabase;

public class MainActivity extends AppCompatActivity implements Connection.MusicParsedListener{

    public static final String FILENAME1 = "PROJECT3.FILENAME1";
    public static final String FILENAME2 = "PROJECT3.FILENAME2";
    public static final String ID = "PROJECT3.ID";
    public static final String JSON = "PROJECT3.JSON";
    public static final String SOUNDEFFECT = "PROJECT3.SOUNDEFFECT";
    public static final int RC_MUSICLIST = 0;
    public static final int RC_PREFERENCE = 1;
    public static final int DEFAULT_ID = 0;

    static final String TAG = "MainActivity";

    private static GameMusicPlayer mPlayer;
    private Connection mConnection;
    private String mMusicJson;
    private Music mCurrentMusic;
    private boolean mImageReady, mBound, mSoundEffect;
    private ConfigDatabase mDatabase;
    private SoundPool mSoundPool;
    private int mSoundId;
    private ProgressDialog mLoadingDialog;

    private TextView mTrackName, mGameName, mArtistName;
    private ImageView mArtwork;
    private ImageButton mPlayButton;

    private MyService mService;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize variables
        mMusicJson = null;
        mConnection = new Connection(this);
        mTrackName = (TextView)findViewById(R.id.text_track_name_main);
        mGameName = (TextView) findViewById(R.id.text_game_name_main);
        mArtistName = (TextView)findViewById(R.id.text_artist_name_main);
        mArtwork = (ImageView) findViewById(R.id.image_album_main);
        mPlayButton = (ImageButton) findViewById(R.id.image_play_music_main);
        mImageReady = false;
        mBound = false;
        mDatabase = new ConfigDatabase(this);
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        mLoadingDialog = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
        mLoadingDialog.setMessage("Loading Data from Server");
        //Initialize Service Connection for Service
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((MyService.LocalBinder)service).getService();
                if(mService.isPlaying()){
                    mPlayButton.setImageResource(R.drawable.ic_pause_black_24dp);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };
        //If the database is not empty, there is a config saved, so load config from database
        //Otherwise, download new JSON file
        if(mDatabase.hasConfig()){
            Config config = mDatabase.getConfig();
            mMusicJson = config.getJson();
            mSoundEffect = config.getSoundEnabled();
            if(mConnection.checkImages(MusicJsonParser.getImageFilenames(mMusicJson))){
                mImageReady = true;
                initMusic(config.getLast());
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.dialog_failed).setTitle(R.string.dialog_failed_title)
                        .setPositiveButton(R.string.dialog_failed_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getBaseContext(),MyService.class);
                                stopService(intent);
                                mDatabase.reset();
                                finish();
                            }
                        }).create().show();
            }
        }else{
            mSoundEffect = true;
            mConnection.downloadJson();
            mLoadingDialog.show();
        }
        //Initialize sound effect file
        try {
            AssetFileDescriptor afd = getAssets().openFd("se.wav");
            mSoundId = mSoundPool.load(afd,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Inflate Option Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_list_music_main,menu);
        return true;
    }

    //Option menu selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean handled;
        switch(item.getItemId()){
            //List of music
            case R.id.menu_list_music:
                handled = true;
                if(mImageReady){
                    Intent intent = new Intent(MainActivity.this, MusicListActivity.class);
                    intent.putExtra(JSON,mMusicJson);
                    startActivityForResult(intent,RC_MUSICLIST);
                }else{
                    Toast.makeText(this, R.string.toast_not_ready,Toast.LENGTH_SHORT).show();
                }
                break;
            //Preference
            case R.id.menu_preference:
                handled = true;
                Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
                intent.putExtra(SOUNDEFFECT,mSoundEffect);
                startActivityForResult(intent,RC_PREFERENCE);
            default:
                handled = super.onOptionsItemSelected(item);
                break;
        }
        return handled;
    }

    //Play/Pause
    public void toggle(View view){
        GameMusicPlayer player = mService.getPlayer();
        if(player.isPlaying()){
            player.pause();
            mPlayButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }else{
            player.start();
            mPlayButton.setImageResource(R.drawable.ic_pause_black_24dp);
        }
    }

    //Next song
    public void next(View view){
        //If sound effect is enabled, play sound effect
        if(mSoundEffect){
            mSoundPool.play(mSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
        if(mCurrentMusic.getId() + 1 < MusicJsonParser.getMusicSize(mMusicJson)){
            initMusic(mCurrentMusic.getId() + 1);
        }else{
            Toast.makeText(this, "This is the last file in the library", Toast.LENGTH_SHORT).show();
        }
    }

    //Previous song
    public void prev(View view){
        if(mSoundEffect){
            mSoundPool.play(mSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
        if(mCurrentMusic.getId() > 0){
            initMusic(mCurrentMusic.getId() - 1);
        }else{
            Toast.makeText(this, "This is the first file in the library", Toast.LENGTH_SHORT).show();
        }
    }

    //Prepare Music instance by the id of the song selected
    public void initMusic(int id){
        try {
            mLoadingDialog.show();
            JSONArray jsonArray = new JSONArray(mMusicJson);
            JSONObject musArray = jsonArray.getJSONObject(id);
            mCurrentMusic = new Music(musArray.getInt("id"),musArray.getString("title")
                    ,musArray.getString("game"),musArray.getString("composer")
                    ,musArray.getString("file1"),musArray.getString("file2")
                    ,musArray.getString("artwork"));
            downloadFiles(mCurrentMusic.getDownloadFilenames());
        } catch (JSONException e) {
            e.printStackTrace();
            mLoadingDialog.dismiss();
        }
    }

    public void downloadFiles(String[] filenames){

        mConnection.downloadFiles(filenames);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            Log.d(TAG, "Received Result");
            //If the music list returned the selected music ID, prepare and play it
            if(requestCode == RC_MUSICLIST) {
                initMusic(data.getIntExtra(ID, DEFAULT_ID));
            }
            //Update preferences
            if(requestCode == RC_PREFERENCE){
                //If user chooses to update library, re-download JSON
                if(data.getBooleanExtra(PreferenceActivity.EXTRA_UPDATELIBRARY,false)){
                    Toast.makeText(this,"Updating Library from online...", Toast.LENGTH_LONG).show();
                    mConnection.downloadJson();
                }else{
                    //If user does not choose to update library but the resultCode is OK,
                    //then user must choose toggle sound effect
                    mSoundEffect = !mSoundEffect;
                    Toast.makeText(this,(mSoundEffect? "Enabled":"Disabled") + " Sound Effect",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Called when JSON file is downloaded
    @Override
    public void jsonParsed(String json)  {
        mMusicJson = json;
        mConnection.downloadImages(MusicJsonParser.getImageFilenames(json));
        initMusic(DEFAULT_ID);
    }

    //Called when JSON file is failed to be downloaded
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

    //Called when music file is downloaded
    @Override
    public void musicParsed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Set metadata
                mTrackName.setText(mCurrentMusic.getName());
                mGameName.setText(mCurrentMusic.getGame());
                mArtistName.setText(mCurrentMusic.getArtist());
                mArtwork.setImageURI(Uri.parse(getExternalCacheDir()+mCurrentMusic.getImageFilename()));
                //Send music to the player Service
                Intent intent = new Intent(getBaseContext(),MyService.class);
                intent.putExtra(FILENAME1,mCurrentMusic.getFiles()[0]);
                intent.putExtra(FILENAME2,mCurrentMusic.getFiles()[1]);
                intent.putExtra(ID,mCurrentMusic.getId());
                if(!isMyServiceRunning(MyService.class)) {
                    startService(intent);
                    bindService(intent,mServiceConnection, Context.BIND_WAIVE_PRIORITY);
                    mBound = true;
                }else{
                    if (!mBound){
                        bindService(intent,mServiceConnection, Context.BIND_WAIVE_PRIORITY);
                        mBound = true;
                    }else {
                        mPlayButton.setImageResource(R.drawable.ic_pause_black_24dp);
                        mService.updatePlayer(mCurrentMusic.getId(), mCurrentMusic.getFiles()[0], mCurrentMusic.getFiles()[1]);
                    }
                }
                mLoadingDialog.dismiss();
            }
        });

    }

    //Called when images are parsed
    @Override
    public void imagesParsed() {
        mImageReady = true;
    }

    //Save config to database when destroyed
    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(mServiceConnection);
        int se = 0;
        if(mSoundEffect){
            se = 1;
        }
        if(mDatabase.hasConfig()){
            mDatabase.update(new Config(mMusicJson,mCurrentMusic.getId(),se));
        }else{
            mDatabase.add(new Config(mMusicJson,mCurrentMusic.getId(),se));
        }
    }

    //Check if the service is running
    //Cite: https://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
