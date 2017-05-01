package du.yufei.vgmplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicRecyclerFragment extends Fragment {

    RecyclerView mRecyclerView;

    public MusicRecyclerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_recycler, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_fragment);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Bundle argument = getArguments();
        String json = argument.getString(MainActivity.JSON);
        MusicRecyclerAdapter adaptere = new MusicRecyclerAdapter(MusicJsonParser.parse(json),(MusicListActivity)getActivity());
        mRecyclerView.setAdapter(adaptere);
        return view;
    }

}
