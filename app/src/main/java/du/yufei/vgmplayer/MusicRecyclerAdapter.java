package du.yufei.vgmplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by edwar on 5/1/2017.
 */

public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerHolder>{

    private List<Music> mMusicList;
    private MusicRecyclerHolder.MusicSelectionListener mListener;


    public MusicRecyclerAdapter(List<Music> list, MusicRecyclerHolder.MusicSelectionListener listener){
        mListener = listener;
        mMusicList = list;
    }

    @Override
    public MusicRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = (LayoutInflater.from(parent.getContext())).inflate(R.layout.view_recycler_item,parent,false);
        MusicRecyclerHolder holder = new MusicRecyclerHolder(view,mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MusicRecyclerHolder holder, int position) {
        holder.bind(mMusicList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }
}
