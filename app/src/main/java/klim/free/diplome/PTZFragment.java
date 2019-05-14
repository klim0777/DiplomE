package klim.free.diplome;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings({"FieldCanBeLocal", "unused", "NullableProblems", "ConstantConditions"})
public class PTZFragment extends Fragment implements SimplePostTask.CallBack {

    private final static String TAG =  "TAG";

    private Button buttonZoomIn, buttonZoomOut;

    private int xStart, yStart;

    private double currSpeedX, currSpeedY;

    private String mServer, mPort;

    private Integer mCameraNum;

    private TextView xSpeedView, ySpeedView;

    private ImageView mArrowView;

    private double degree;

    public PTZFragment() {
        // Required empty public constructor
    }

    public void setServerAndPort(String server, String port) {
        mServer = server;
        mPort = port;
    }

    public void setCameraNumber(Integer number) {
        mCameraNum = number;
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

        final SimplePostTask.CallBack callBack = this;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN):
                        Log.d(TAG, "action down");

                        xStart = (int) event.getX();
                        yStart = (int) event.getY();

                        mArrowView.setX( (float) xStart);
                        mArrowView.setY( (float) yStart);

                        currSpeedX = 0.0;
                        currSpeedY = 0.0;

                        xSpeedView.setText("x : 0.0");
                        ySpeedView.setText("y : 0.0");

                        return true;
                    case (MotionEvent.ACTION_MOVE):

                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        tracking(x,y);

                        Log.d(TAG, "action move");
                        return true;
                    case (MotionEvent.ACTION_UP):

                        Log.d(TAG, "action up");

                        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
                        Matrix mat = new Matrix();
                        mat.postRotate( 0);
                        Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
                        mArrowView.setImageBitmap(bMapRotate);

                        xSpeedView.setText("x :");
                        ySpeedView.setText("y :");

                        // move stop
                        if (mCameraNum != null) {
                            new SimplePostTask(callBack)
                                    .setServerAndPort(mServer, mPort)
                                    .execute("MoveStop?number=" + mCameraNum);
                        } else {
                            exceptionCatched(": camera not selected");
                        }
                        return true;
                }
                return false;
                }
        });

        buttonZoomOut = view.findViewById(R.id.buttonZoomOut);
        buttonZoomIn = view.findViewById(R.id.buttonZoomIn);

        xSpeedView = view.findViewById(R.id.xSpeedTextView);
        ySpeedView = view.findViewById(R.id.ySpeedTextView);

        mArrowView = view.findViewById(R.id.arrow_view);

        buttonZoomIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case (MotionEvent.ACTION_DOWN) :

                        if (mCameraNum != null) {
                            new SimplePostTask(callBack)
                                    .setServerAndPort(mServer,mPort)
                                    .execute("ZoomIn?number=" + mCameraNum);
                        } else {
                            exceptionCatched(": camera not selected");
                        }

                        Log.d(TAG,"down");
                        return true;
                    case (MotionEvent.ACTION_MOVE) :
                        return true;
                    case (MotionEvent.ACTION_UP) :
                        if (mCameraNum != null) {
                            new SimplePostTask(callBack)
                                    .setServerAndPort(mServer,mPort)
                                    .execute("MoveStop?number=" + mCameraNum);
                        } else {
                            exceptionCatched(": camera not selected");
                        }
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

                        if (mCameraNum != null) {
                            new SimplePostTask(callBack)
                                    .setServerAndPort(mServer,mPort)
                                    .execute("ZoomOut?number=" + mCameraNum);
                        } else {
                            exceptionCatched(": camera not selected");
                        }

                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        return true;
                    case (MotionEvent.ACTION_UP):

                        if (mCameraNum != null) {
                            new SimplePostTask(callBack)
                                    .setServerAndPort(mServer,mPort)
                                    .execute("MoveStop?number=" + mCameraNum);
                        } else {
                            exceptionCatched(": camera not selected");
                        }

                        return true;
                }
                return false;
            }
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
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG,"FUCK");
        }
    }

    private void tracking(int x, int y) {

        boolean changed = false;

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int diffX = x - xStart;
        int diffY = y - yStart;

        double a = Math.atan2(diffX,-diffY);

        degree =  Math.toDegrees(a);

        double speedX = (double) diffX * 3 / (double) width;
        double speedY = (double) diffY * 3 / (double) height;

        double speedXFormatted = Math.round(speedX * 10) / 10.0;
        double speedYFormatted = Math.round(speedY * 10) / 10.0;

        Log.d(TAG,"speedX : " + speedXFormatted);
        Log.d(TAG,"speedY : " + speedYFormatted);

        if (currSpeedX == speedXFormatted) {
            Log.d(TAG,"x still the same : " + currSpeedX + " " + speedXFormatted);
        } else {
            currSpeedX = speedXFormatted;
            changed = true;
            Log.d(TAG,"x changed");
        }

        if (currSpeedY == speedYFormatted) {
            Log.d(TAG,"y still the same : " + currSpeedX + " " + speedXFormatted);
        } else {
            currSpeedY = speedYFormatted;
            changed = true;
            Log.d(TAG,"y changed");
        }

        if (changed) {
            if (speedXFormatted > 1) {
                speedXFormatted = 1.0;
            } else if (speedXFormatted < -1.0) {
                speedXFormatted = -1.0;
            }

            if (speedYFormatted > 1) {
                speedYFormatted = 1.0;
            } else if (speedYFormatted < -1.0) {
                speedYFormatted = -1.0;
            }

            xSpeedView.setText("x : " + speedXFormatted);
            ySpeedView.setText("y : " + speedYFormatted);

            if (mCameraNum !=  null) {
                new MoveTask()
                        .setNumber(mCameraNum)
                        .setServerAndPort(mServer,mPort)
                        .execute(speedXFormatted,-speedYFormatted);
            } else {
                exceptionCatched(": camera not selected");
            }
        }


        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);
        Matrix mat = new Matrix();
        mat.postRotate( (float) degree );
        Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
        mArrowView.setImageBitmap(bMapRotate);
    }


}
