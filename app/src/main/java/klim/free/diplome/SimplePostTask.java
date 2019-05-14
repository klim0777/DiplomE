package klim.free.diplome;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings({"WeakerAccess", "StringBufferMayBeStringBuilder", "UnusedAssignment", "unused"})
public class SimplePostTask extends AsyncTask<String, Void, String> {

    interface CallBack {
        void exceptionCatched(String message);
    }

    private String mUrl;
    private String mMethod = "";

    CallBack mCallback;

    SimplePostTask(CallBack callBack) {
        mCallback = callBack;
    }

    public SimplePostTask setServerAndPort(String server, String port) {
        mUrl = "http://" + server + ":" + port + "/";
        return this;
    }

    public SimplePostTask setMethod(String method) {
        mMethod = method;
        return this;
    }

    // get JSONArray
    @Override
    protected String doInBackground(String... params) {

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

            if (mMethod.equals("")) {
                Log.d("TAG","method was null, has been set to POST");
                connection.setRequestMethod("POST");
            } else {
                Log.d("TAG","GET method");
                connection.setRequestMethod(mMethod);
            }

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String finalJson = buffer.toString();

            Log.d("TAG","responce " + finalJson);

            return finalJson;

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
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
    }

}


