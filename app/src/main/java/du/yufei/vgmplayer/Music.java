package du.yufei.vgmplayer;

/**
 * Created by edwar on 5/1/2017.
 */

public class Music {
    private String mName, mGame, mArtist, mFile1, mFile2, mImageFile;
    private int mId;

    public Music(int id, String name, String game, String artist, String file1, String file2, String image){
        mId = id;
        mName = name;
        mGame = game;
        mArtist = artist;
        mFile1 = file1;
        mFile2 = file2;
        mImageFile = image;
    }

    public int getId(){
        return mId;
    }

    public String getName(){
        return mName;
    }

    public String getGame(){
        return mGame;
    }

    public String getArtist(){
        return mArtist;
    }

    public String[] getFiles(){
        return new String[]{mFile1,mFile2};
    }

    public String getImageFilename(){
        return mImageFile;
    }

    public String[] getDownloadFilenames(){
        return new String[]{mFile1,mFile2,mImageFile};
    }
}
