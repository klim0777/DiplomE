package klim.free.diplome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class CamerasFragment extends Fragment
                                implements DiscoveryTask.NotifyAdapter,
                                           SwipeRefreshLayout.OnRefreshListener {
    private final static String TAG =  "TAG";

    private List<Camera> mCameraList;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mCameraListView;
    private FloatingActionButton floatingActionButton;
    private TextView mSwipeHint;

    private CameraAdapter mAdapter;

    @Override
    public void doIt() {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }

    public CamerasFragment() {
        // Required empty public constructor
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
        floatingActionButton = view.findViewById(R.id.fab);
        mSwipeHint = view.findViewById(R.id.swipe_hint);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new CameraAdapter(this.getActivity(),mCameraList);
        mCameraListView.setAdapter(mAdapter);


        final DiscoveryTask.NotifyAdapter callback = this;

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // manually add camera
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
        new DiscoveryTask(mCameraList,this).execute();
    }
}
