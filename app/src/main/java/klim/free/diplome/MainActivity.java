package klim.free.diplome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class  MainActivity extends AppCompatActivity
        implements SimpleTask.CallBack,
                   CamerasFragment.CameraSelected {

    private final static String TAG = "TAG";

    private BottomNavigationView mBottomNavigation;

    private PTZFragment ptzFragment;
    private PresetsFragment presetsFragment;
    private CamerasFragment camerasFragment;

    private TextView mCameraSelected;

    // list for all classes
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
        // retrieve ip port data
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

        Log.d(TAG,"mainActivity onResume(), server : " + mServer + ", port :" + mPort);

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

    // SimpleTask.Callback
    @Override
    public void exceptionCatched(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Unable to perform request " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void cameraAdded() {

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

                intent.putExtra("IP",mServer);
                intent.putExtra("Port",mPort);

                startActivity(intent);
                return true;
            case R.id.action_about_developers :
                Intent intent2 = new Intent(this,AboutActivity.class);
                startActivity(intent2);
                return true;
        }
        return false;
    }

    // CameraFragment.Callback
    @SuppressLint("SetTextI18n")
    @Override
    public void cameraSelectedCallback(String IP, Integer number) {
        mCameraSelected.setText("Selected camera : " + IP);
        mCameraNum = number;
        ptzFragment.setCameraNumber(mCameraNum);
        presetsFragment.setCameraNumber(mCameraNum);
    }
}
