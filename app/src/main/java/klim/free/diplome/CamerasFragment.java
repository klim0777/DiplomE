package klim.free.diplome;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantCast", "NullableProblems"})
public class CamerasFragment extends Fragment
                                implements DiscoveryTask.DiscoveryTaskCallback,
                                           SwipeRefreshLayout.OnRefreshListener,
                                           SimplePostTask.CallBack {

    interface CameraSelected {
        void cameraSelectedCallback(String IP, Integer number);
    }

    private final static String TAG =  "TAG";

    private CameraSelected mCallback;

    private String mServer, mPort;

    private List<Camera> mCameraList;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mCameraListView;
    private FloatingActionButton floatingActionButton;
    private TextView mSwipeHint;

    private CameraAdapter mAdapter;

    public void setServerAndPort(String server, String port) {
        mServer = server;
        mPort = port;
    }

    public void setCallback(CameraSelected callback) {
        mCallback = callback;
    }

    public void setList(List<Camera> list) {
        mCameraList = list;
    }

    public static CamerasFragment newInstance(String param1, String param2) {
        CamerasFragment fragment = new CamerasFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public CamerasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cameras, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mCameraListView = view.findViewById(R.id.camera_list_view);

        mSwipeHint = view.findViewById(R.id.swipe_hint);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new CameraAdapter(this.getActivity(),mCameraList);
        mCameraListView.setAdapter(mAdapter);


        final DiscoveryTask.DiscoveryTaskCallback callback = this;

        final SimplePostTask.CallBack callBack = this;

        mCameraListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Camera cameraToConnect = mCameraList.get(position);
                Log.d(TAG,"tapped on camera num#" + cameraToConnect.getNumber());

                mCallback.cameraSelectedCallback(cameraToConnect.getIp(), cameraToConnect.getNumber());
            }
        });

        return view;
    }



    @Override
    public void onResume() {
        Log.d(TAG,"camerasFragment onResume");
        if (mCameraList.size() == 0) {
            mSwipeHint.setVisibility(View.VISIBLE);
        } else {
            mSwipeHint.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onRefresh() {
        new DiscoveryTask(mCameraList,this)
                .setServerAndPort(mServer,mPort)
                .execute();
    }

    @SuppressWarnings("ConstantConditions")
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

    // DiscoveryTask callback
    @Override
    public void success() {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeHint.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }
    // Discovery task callback
    @Override
    public void error() {
        mSwipeRefreshLayout.setRefreshing(false);
        exceptionCatched(": discovery failed");
    }

}
