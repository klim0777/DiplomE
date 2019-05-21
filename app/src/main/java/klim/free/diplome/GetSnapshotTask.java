package klim.free.diplome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetSnapshotTask extends AsyncTask<String, Void, Bitmap> {

    interface SnapshotCallBack {
        void exceptionCatched(String message);
        void snapshotRecieved(Bitmap bitmap);
    }

    private String mUrl;
    private Bitmap mBitmap;

    SnapshotCallBack mCallback;


    GetSnapshotTask(SnapshotCallBack callBack) {
        mCallback = callBack;
    }

    public GetSnapshotTask setServerAndPort(String server, String port) {
        mUrl = "http://" + server + ":" + port + "/";
        return this;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        String url = mUrl;
        url = url + params[0];
        Log.d("TAG","url : " + url);
        try {
            URL urlFinal = new URL(url);
            connection = (HttpURLConnection) urlFinal.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestMethod("POST");

            InputStream stream = connection.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(stream);
            mBitmap = BitmapFactory.decodeStream(bis);
            bis.close();

            Log.d("TAG","responce " + mBitmap.getWidth());

            return mBitmap;

        } catch (MalformedURLException e) {
            Log.d("TAG","malformed ");
            mCallback.exceptionCatched(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("TAG","io ");
            mCallback.exceptionCatched(e.getMessage());
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  null;
    }

    @Override
    protected void onPostExecute(Bitmap response) {
        super.onPostExecute(response);
        if (response != null) {
            mCallback.snapshotRecieved(mBitmap);
        } else {
            mCallback.exceptionCatched(" responce null");
        }

    }

}
