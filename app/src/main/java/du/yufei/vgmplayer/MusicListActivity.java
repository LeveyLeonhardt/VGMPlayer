package du.yufei.vgmplayer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MusicListActivity extends AppCompatActivity implements MusicRecyclerHolder.MusicSelectionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);
        Fragment fragment = new MusicRecyclerFragment();
        String json = getIntent().getStringExtra(MainActivity.JSON);
        Bundle arg = new Bundle();
        arg.putString(MainActivity.JSON,json);
        fragment.setArguments(arg);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.frame_music_list,fragment).commit();
    }

    @Override
    public void onSelected(Music music) {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.ID,music.getId());
        setResult(RESULT_OK,intent);
        finish();
    }
}
