package du.yufei.vgmplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PreferenceActivity extends AppCompatActivity {

    public static final String EXTRA_UPDATELIBRARY = "PROJECT3.EXTRA_UPDATELIBRARY";
    public static final String EXTRA_TOGGLESOUNDEFFECT = "PROJECT3.EXTRA_TOGGLESOUNDEFFECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        ((Button)findViewById(R.id.button_sound_preference)).setText(
                (getIntent().getBooleanExtra(MainActivity.SOUNDEFFECT,true)?"Disable":"Enable")+" Sound Effect");
    }

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

    public void seClicked(View view){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_UPDATELIBRARY,false);
        intent.putExtra(EXTRA_TOGGLESOUNDEFFECT,true);
        setResult(RESULT_OK,intent);
        finish();
    }
}
