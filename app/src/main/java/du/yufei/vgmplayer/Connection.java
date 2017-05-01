package du.yufei.vgmplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by edwar on 5/1/2017.
 */

public class Connection {

    public interface MusicParsedListener{
        public void jsonParsed(String json);
        public void jsonFailed();
        public void musicParsed();
    }

    public final static String HOSTNAME = "http://www.edward.moe/csc214/";
    public final static String JSONFILENAME = "music.json";

    private MusicParsedListener mListener;
    private String mJson;

    public Connection(Context context){
        mListener = (MusicParsedListener) context;
        new GetJsonFromServer().execute();
    }

    public void downloadFile(String filename){
        new GetFileFromServer().execute(filename);
    }

    private class GetJsonFromServer extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params){
            String json = null;
            HttpURLConnection connection;
            BufferedReader reader = null;
            try{
                URL url = new URL(HOSTNAME+JSONFILENAME);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                json = reader.readLine();
            }catch(Exception e){
                e.printStackTrace();
                mListener.jsonFailed();
            }finally{
                if(reader != null){
                    try{
                        reader.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            mJson = s;
            mListener.jsonParsed(s);
        }
    }

    private class GetFileFromServer extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params){
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(HOSTNAME+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                //Used buffered input stream and output stream to ensure that mp3 file get transferred correctly
                //Cite: https://stackoverflow.com/questions/22326796/android-download-file-get-corrupted
                BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                File file = new File(((Context)mListener).getExternalCacheDir()+params[0]);
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file,false);
                BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream);

                byte[] buffer = new byte[16384];
                int length = 0;
                while((length = inputStream.read(buffer,0,16384))!=-1){
                    outputStream.write(buffer,0,length);
                }
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            }catch(Exception e){
                e.printStackTrace();
                mListener.jsonFailed();
            }finally{
                try{
                    if(reader != null){
                        reader.close();
                    }
                    if(connection != null){
                        connection.disconnect();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s){
            mListener.musicParsed();
        }
    }
}
