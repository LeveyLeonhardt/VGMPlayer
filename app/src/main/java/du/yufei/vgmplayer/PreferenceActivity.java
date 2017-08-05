package du.yufei.vgmplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreferenceActivity extends AppCompatActivity {

    public static final String EXTRA_UPDATELIBRARY = "VGMPLAYER.EXTRA_UPDATELIBRARY";
    public static final String EXTRA_TOGGLESOUNDEFFECT = "VGMPLAYER.EXTRA_TOGGLESOUNDEFFECT";
    public static final String EXTRA_UPDATEHOSTNAME = "VGMPLAYER.EXTRA_UPDATEHOSTNAME";
    public static final String EXTRA_HOSTNAME = "VGMPLAYER.EXTRA_HOSTNAME";


    private String mCameraFilePath;

    static final int RC_CAMERAINTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        ((Button)findViewById(R.id.button_sound_preference)).setText(
                (getIntent().getBooleanExtra(MainActivity.SOUNDEFFECT,true)?
                        getResources().getString(R.string.button_setting_sound_effect_disable):
                        getResources().getString(R.string.button_setting_sound_effect_enable)));
    }

    //Re-Download the json file to update library
    //Show an AlertDialog to warn user that this will reset the player
    public void updateClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(PreferenceActivity.this);
        builder.setMessage(R.string.dialog_warning_update).setTitle(R.string.dialog_warning_title)
                .setPositiveButton(R.string.dialog_warning_pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_UPDATELIBRARY,true);
                        intent.putExtra(EXTRA_TOGGLESOUNDEFFECT,false);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }).setNegativeButton(R.string.dialog_warning_neg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    //Enable/Disable sound effect
    public void seClicked(View view){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATELIBRARY,false);
        intent.putExtra(EXTRA_TOGGLESOUNDEFFECT,true);
        setResult(RESULT_OK,intent);
        finish();
    }

    //Update Library Hostname
    public void hostUpdate(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(PreferenceActivity.this);
        final EditText hostnameText = new EditText(this);
        hostnameText.setHint("New Hostname");
        builder.setMessage(R.string.dialog_hostname_update_message).setTitle(R.string.dialog_hostname_update_title)
                .setView(hostnameText)
                .setPositiveButton(R.string.dialog_warning_pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_UPDATELIBRARY,false);
                        intent.putExtra(EXTRA_TOGGLESOUNDEFFECT,false);
                        intent.putExtra(EXTRA_UPDATEHOSTNAME, true);
                        intent.putExtra(EXTRA_HOSTNAME, hostnameText.getText().toString());
                        //TODO: Validate URL
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }).setNegativeButton(R.string.dialog_warning_neg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

}
