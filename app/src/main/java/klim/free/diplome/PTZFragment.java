package klim.free.diplome;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

@SuppressWarnings({"FieldCanBeLocal", "unused", "NullableProblems", "ConstantConditions"})
public class PTZFragment extends Fragment implements SimplePostTask.CallBack {
    private final static String TAG =  "TAG";

    private Button buttonZoomIn, buttonZoomOut;

    public PTZFragment() {
        // Required empty public constructor
    }

    private String mServer, mPort;

    public void setServerAndPort(String server, String port) {
        mServer = server;
        mPort = port;
    }

    public static PTZFragment newInstance(String param1, String param2) {
        PTZFragment fragment = new PTZFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"ptzFragment onCreate");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"ptzFragment onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ptz, container, false);

        buttonZoomOut = view.findViewById(R.id.buttonZoomOut);
        buttonZoomIn = view.findViewById(R.id.buttonZoomIn);

        final SimplePostTask.CallBack callBack = this;


        buttonZoomIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        new SimplePostTask(callBack)
                                .setServerAndPort(mServer,mPort)
                                .execute("ZoomIn");
                        Log.d(TAG,"down");
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        Log.d(TAG,"move");
                        return true;
                    case (MotionEvent.ACTION_UP):
                        new SimplePostTask(callBack)
                                .setServerAndPort(mServer,mPort)
                                .execute("MoveStop");
                        // moveStop
                        Log.d(TAG,"up");
                        return true;
                }
                return false;
            }
        });

        buttonZoomOut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        new SimplePostTask(callBack)
                                .setServerAndPort(mServer,mPort)
                                .execute("ZoomOut");
                        // new SimplePostTask(callBack).execute("ZoomOut");
                        Log.d(TAG,"down");
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        Log.d(TAG,"move");
                        return true;
                    case (MotionEvent.ACTION_UP):
                        new SimplePostTask(callBack)
                                .setServerAndPort(mServer,mPort)
                                .execute("MoveStop");
                        Log.d(TAG,"up");
                        return true;
                }
                return false;            }
        });

        return view;
    }



    @Override
    public void onResume() {
        Log.d(TAG,"ptzFragment resume");
        super.onResume();
    }

    @Override
    public void exceptionCatched(final String message) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Unable to perform request " + message,
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG,"FUCK");
        }
    }
}
