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

@SuppressWarnings({"UnnecessaryLocalVariable", "StringBufferMayBeStringBuilder", "UnusedAssignment", "Convert2Diamond"})
public class DiscoveryTask  extends AsyncTask<Double, Void, String> {

    private List<Camera> mCameraList;
    private String mUrl;

    DiscoveryTask(List<Camera> list, NotifyAdapter callback) {
        mCameraList = list;
        mCallback = callback;

        mCameraList.clear();
    }

    interface NotifyAdapter {
        void doIt();
    }

    private NotifyAdapter mCallback;

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

            //connection.connect();

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
        Log.d("TAG","response " + response);

        if (response == null) {
            return;
        }

        List<String> list = new ArrayList<String>(Arrays.asList(response.split(",")));

        for (int i = 0; i < list.size() - 1; i++) {
            if (i % 2 == 0) {
                Camera buff = new Camera(list.get(i),list.get(i+1));
                if (!mCameraList.contains(buff)) {
                    mCameraList.add(buff);
                }
            }
        }

        mCallback.doIt();
    }

}

