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

@SuppressWarnings({"StringBufferMayBeStringBuilder", "UnusedAssignment", "WeakerAccess"})
public class MoveTask extends AsyncTask<Double, Void, String> {

    private String mUrl;
    private Integer mNumber;

    public MoveTask setServerAndPort(String server, String port) {
        mUrl = "http://" + server + ":" + port + "/ContinuousMove?x=";
        return this;
    }

    public MoveTask setNumber(Integer number) {
        mNumber = number;
        return this;
    }

    // get JSONArray
    @Override
    protected String doInBackground(Double... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String url = mUrl;
        url = url + params[0];
        url = url + "&y=";
        url = url + params[1];
        url = url + "&number=" +  mNumber;
        Log.d("MOVE","url : " + url);
        try {
            URL urlFinal = new URL(url);
            connection = (HttpURLConnection) urlFinal.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            String responce = buffer.toString();

            Log.d("MOVE","responce "  + responce);

            return responce;

        } catch (MalformedURLException e) {
            Log.d("TAG","malformed ");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("TAG","io ");
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

