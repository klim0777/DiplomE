package klim.free.diplome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class  MainActivity extends AppCompatActivity
        implements SimplePostTask.CallBack,
                   CamerasFragment.CameraSelected {

    private final static String TAG = "TAG";

    private BottomNavigationView mBottomNavigation;

    private PTZFragment ptzFragment;
    private PresetsFragment presetsFragment;
    private CamerasFragment camerasFragment;

    private TextView mCameraSelected;

    private List<Camera> mCameraList = new ArrayList<>();

    private String mServer = "188.246.233.224";
    private String mPort = "8080";

    private Integer mCameraNum;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.action_ptz:
                    fragment = ptzFragment;
                    break;
                case R.id.action_presets:
                    fragment = presetsFragment;
                    break;
                case R.id.action_cameras:
                    fragment = camerasFragment;
                    break;
            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ptzFragment = new PTZFragment();
        presetsFragment = new PresetsFragment();
        camerasFragment = new CamerasFragment();

        ptzFragment.setServerAndPort(mServer,mPort);
        presetsFragment.setServerAndPort(mServer,mPort);
        camerasFragment.setServerAndPort(mServer,mPort);

        camerasFragment.setList(mCameraList);
        camerasFragment.setCallback(this);

        loadFragment(ptzFragment);

        mBottomNavigation = findViewById(R.id.bottomNavigationView);
        mBottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mBottomNavigation.setSelectedItemId(R.id.action_cameras);

        mCameraSelected = findViewById(R.id.camera_selected_indicator);

    }


    @Override
    protected void onResume() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String IP = extras.getString("IP");
            String Port = extras.getString("Port");

            Log.d(TAG,"retrieved " + IP + "," + Port);

            mPort = Port;
            mServer = IP;
            ptzFragment.setServerAndPort(mServer,mPort);
            presetsFragment.setServerAndPort(mServer,mPort);
            camerasFragment.setServerAndPort(mServer,mPort);

        }
        Log.d(TAG,"mainActivity onResume(), server :" + mServer + ", port :" + mPort);

        super.onResume();
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }

        return false;
    }
/*
    @SuppressWarnings("deprecation")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                xStart = (int) event.getX();
                yStart = (int) event.getY();
                currSpeedX = 0.0;
                currSpeedY = 0.0;
                new SimplePostTask(this)
                        .setServerAndPort(mServer,mPort)
                        .execute("SetFlag");
                return true;
            case (MotionEvent.ACTION_MOVE):
                int x = (int) event.getX();
                int y = (int) event.getY();
                tracking(x,y);
                return true;
            case (MotionEvent.ACTION_UP):
                new SimplePostTask(this)
                        .setServerAndPort(mServer,mPort)
                        .execute("UnsetFlag?number=" + mCameraNum);
                               return true;
            default:
                return super.onTouchEvent(event);
        }

    }
*/

    @Override
    public void exceptionCatched(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Unable to perform request " + message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_server :
                Intent intent = new Intent(this,SelectServerActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about_developers :
                Intent intent2 = new Intent(this,AboutActivity.class);
                startActivity(intent2);
                return true;
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void cameraSelectedCallback(String IP, Integer number) {
        mCameraSelected.setText("Selected camera : " + IP);
        mCameraNum = number;
        ptzFragment.setCameraNumber(mCameraNum);
        presetsFragment.setCameraNumber(mCameraNum);
    }
}
