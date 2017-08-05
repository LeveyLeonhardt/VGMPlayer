package du.yufei.vgmplayer;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by edwar on 5/1/2017.
 */

public class MusicRecyclerHolder extends RecyclerView.ViewHolder{

    private final String TAG = "MusicRecyclerHolder";

    public interface MusicSelectionListener{
        void onSelected(Music music);
    }

    private ImageView mArtwork;
    private TextView mTrackText, mGameText, mArtistText;
    private Music mMusic;
    private String mCacheDir;
    private View mView;
    private MusicSelectionListener mListener;

    public MusicRecyclerHolder(View view, final MusicSelectionListener listener){
        super(view);
        mView = view;
        mListener = listener;
        mCacheDir = view.getContext().getApplicationContext().getExternalCacheDir().toString();
        mTrackText = (TextView) view.findViewById(R.id.text_track_name_recycler);
        mGameText = (TextView) view.findViewById(R.id.text_game_name_recycler);
        mArtistText = (TextView) view.findViewById(R.id.text_artist_name_recycler);
        mArtwork = (ImageView) view.findViewById(R.id.image_artwork_recycler);
    }

    public void bind(Music input){
        mMusic = input;
        mTrackText.setText(mMusic.getName());
        mGameText.setText(mMusic.getGame());
        mArtistText.setText(mMusic.getArtist());
        //Log.d("Holder", Uri.parse(Connection.HOSTNAME+mMusic.getImageFilename()).toString());
        mArtwork.setImageURI(Uri.parse(mCacheDir+mMusic.getImageFilename()));
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Clicked");
                mListener.onSelected(mMusic);
            }
        });
    }
}
