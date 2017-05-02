package du.yufei.vgmplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by edwar on 5/1/2017.
 */

public class MusicJsonParser {
    public static List<Music> parse(String json){
        List<Music> musicList = new LinkedList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                Music music = new Music(obj.getInt("id"),obj.getString("title"),obj.getString("game")
                        ,obj.getString("composer"),obj.getString("file1"),obj.getString("file2"),obj.getString("artwork"));
                musicList.add(music);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return musicList;
    }

    public static String[] getImageFilenames(String json){
        String[] filenames;
        try {
            JSONArray jsonArray = new JSONArray(json);
            filenames = new String[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                filenames[i]=obj.getString("artwork");
            }
            return filenames;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getMusicSize(String json){
        try {
            JSONArray jsonArray = new JSONArray(json);
            return jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
