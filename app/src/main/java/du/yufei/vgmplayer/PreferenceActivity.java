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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreferenceActivity extends AppCompatActivity {

    public static final String EXTRA_UPDATELIBRARY = "PROJECT3.EXTRA_UPDATELIBRARY";
    public static final String EXTRA_TOGGLESOUNDEFFECT = "PROJECT3.EXTRA_TOGGLESOUNDEFFECT";

    private String mCameraFilePath;

    static final int RC_CAMERAINTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        ((Button)findViewById(R.id.button_sound_preference)).setText(
                (getIntent().getBooleanExtra(MainActivity.SOUNDEFFECT,true)?"Disable":"Enable")+" Sound Effect");
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

    //Take a picture (Random feature to meet the project requirement)
    public void cameraClicked(View view){
        dispatchTakePictureIntent();
    }

    //Cite: https://developer.android.com/training/camera/photobasics.html
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this,"Failed to save the image",Toast.LENGTH_SHORT);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "du.yufei.vgmplayer.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RC_CAMERAINTENT);
            }
        }
    }

    //Cite: https://developer.android.com/training/camera/photobasics.html
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCameraFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == RC_CAMERAINTENT) {
                Toast.makeText(this,"Image Saved to "+mCameraFilePath,Toast.LENGTH_LONG).show();
            }
        }
    }
}
