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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"UnnecessaryLocalVariable", "StringBufferMayBeStringBuilder", "UnusedAssignment", "Convert2Diamond", "WeakerAccess"})
public class DiscoveryTask  extends AsyncTask<Double, Void, String> {

    private List<Camera> mCameraList;
    private String mUrl;

    DiscoveryTask(List<Camera> list, DiscoveryTaskCallback callback) {
        mCameraList = list;
        mCallback = callback;

        mCameraList.clear();
    }

    interface DiscoveryTaskCallback {
        void success();
        void error(String message);
    }

    private DiscoveryTaskCallback mCallback;

    public DiscoveryTask setServerAndPort(String server, String port) {
        mUrl = "http://" + server + ":" + port + "/Discovery";
        return this;
    }

    @Override
    protected String doInBackground(Double... params) {

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String url = mUrl;
        Log.d("TAG","url : " + url);
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

            String finalJson = buffer.toString();

            return finalJson;
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

        if ( response == null ) {
            mCallback.error("responce was null");
            return;
        } else if (response.length() == 0) {
            mCallback.error("No devices found");
            return;
        } else {
            Log.d("TAG","devices found : " + response.length());
        }

        List<String> list = new ArrayList<String>(Arrays.asList(response.split(",")));

        for (int i = 0; i < list.size() ; i++) {
            if (i % 3 == 0) {
                Camera buff = new Camera(list.get(i), list.get(i+1), Integer.valueOf(list.get(i+2)));
                Log.d("TAG","i = " + i +
                        " camera : " + buff.getIp() +
                        " " +  buff.getPort() + " " + buff.getNumber());
                mCameraList.add(buff);
            }
        }

        mCallback.success();
    }

}

