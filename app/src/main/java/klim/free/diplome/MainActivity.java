package klim.free.diplome;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class  MainActivity extends AppCompatActivity implements SimplePostTask.CallBack {

    private final static String TAG = "TAG";
    private BottomNavigationView mBottomNavigation;

    public static final String URL_BASE = "http://188.246.233.224:8080/";

    private PTZFragment ptzFragment;
    private PresetsFragment presetsFragment;
    private CamerasFragment camerasFragment;

    private int xStart, yStart;
    private double currSpeedX, currSpeedY;

    private List<Camera> mCameraList = new ArrayList<>();

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

        camerasFragment.setList(mCameraList);

        //mCameraList.add(new Camera("192.168.11.12","80"));

        loadFragment(ptzFragment);

        mBottomNavigation = findViewById(R.id.bottomNavigationView);
        mBottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mBottomNavigation.setSelectedItemId(R.id.action_cameras);

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
                new SimplePostTask(this).execute("SetFlag");
                return true;
            case (MotionEvent.ACTION_MOVE):
                int x = (int) event.getX();
                int y = (int) event.getY();
                tracking(x,y);
                return true;
            case (MotionEvent.ACTION_UP):
                new SimplePostTask(this).execute("UnsetFlag");
                // new SimplePostTask(this).execute("GetSnapshot");
                               return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void tracking(int x, int y) {
        boolean changed = false;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int diffX = x - xStart;
        int diffY = y - yStart;

        double speedX = (double) diffX * 3 / (double) width;
        double speedY = (double) diffY * 3 / (double) height;

        DecimalFormat form = new DecimalFormat("0.0");

        double speedXFormatted = Double.valueOf(form.format(speedX));
        double speedYFormatted = Double.valueOf(form.format(speedY));

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

            new MoveTask().execute(speedXFormatted,-speedYFormatted);

        }

    }

    @Override
    public void exceptionCatched(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Unable to perform request" + message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }


}
