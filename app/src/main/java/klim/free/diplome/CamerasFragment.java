package klim.free.diplome;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused", "RedundantCast", "NullableProblems"})
public class CamerasFragment extends Fragment
        implements DiscoveryTask.DiscoveryTaskCallback,
        SwipeRefreshLayout.OnRefreshListener,
        SimpleTask.CallBack {

    interface CameraSelected {
        void cameraSelectedCallback(String IP, Integer number);
    }

    private final static String TAG = "TAG";

    private CameraSelected mCallback;

    private String mServer, mPort;

    private List<Camera> mCameraList;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mCameraListView;
    private FloatingActionButton mAddCameraButton;
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

        final DiscoveryTask.DiscoveryTaskCallback callback = this;
        final SimpleTask.CallBack callBack = this;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cameras, container, false);

        // find all views
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mCameraListView = view.findViewById(R.id.camera_list_view);
        mSwipeHint = view.findViewById(R.id.swipe_hint);
        mAddCameraButton = view.findViewById(R.id.add_camera_button);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new CameraAdapter(this.getActivity(), mCameraList);
        mCameraListView.setAdapter(mAdapter);

        mCameraListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Camera cameraToConnect = mCameraList.get(position);
                Log.d(TAG, "tapped on camera num#" + position);

                mCallback.cameraSelectedCallback(cameraToConnect.getIp(), position);
            }
        });

        mAddCameraButton.setOnClickListener(addCameraListener);

        return view;
    }

    private View.OnClickListener addCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // manually add camera
            LayoutInflater li = LayoutInflater.from(getContext());

            @SuppressLint("InflateParams")
            View promptsView = li.inflate(R.layout.camera_dialog, null);

            //Создаем AlertDialog

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(getContext());

            mDialogBuilder.setView(promptsView);

            //Настраиваем отображение поля для ввода текста в открытом диалоге:

            final EditText ipEditText = (EditText) promptsView.findViewById(R.id.ip_edit_text);

            final EditText portEditText = (EditText) promptsView.findViewById(R.id.port_edit_text);

            final EditText loginEditText = (EditText) promptsView.findViewById(R.id.login_edit_text);

            final EditText passwordEditText = (EditText) promptsView.findViewById(R.id.password_edit_text);

            //Настраиваем сообщение в диалоговом окне:

            mDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String ip = ipEditText.getText().toString();
                                    String port = portEditText.getText().toString();
                                    String login = loginEditText.getText().toString();
                                    String password = passwordEditText.getText().toString();
                                    Camera cameraBuff = new Camera(ip, port, login, password);

                                    new SimpleTask(CamerasFragment.this)
                                            .setServerAndPort(mServer,mPort)
                                            .execute("AddCamera?ip=" + ip
                                            + "&port=" + port + "&login=" + login
                                            + "&password=" + password);

                                }
                            })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

            //Создаем AlertDialog:
            AlertDialog alertDialog = mDialogBuilder.create();
            //и отображаем его:
            alertDialog.show();


        }
    };

    private void doDiscovery() {
        new DiscoveryTask(mCameraList,CamerasFragment.this)
                .setServerAndPort(mServer,mPort)
                .execute();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "camerasFragment onResume");
        if (mCameraList.size() == 0) {
            mSwipeHint.setVisibility(View.VISIBLE);
        } else {
            mSwipeHint.setVisibility(View.GONE);
        }
        super.onResume();
    }

    @Override
    public void onRefresh() {
        doDiscovery();
    }

    // SimpleTask callback
    @SuppressWarnings("ConstantConditions")
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
            Log.d(TAG, "FUCK");
        }
    }

    // SimpleTask callback
    @Override
    public void cameraAdded() {
        //doDiscovery();
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
    public void error(String message) {
        mSwipeRefreshLayout.setRefreshing(false);
        exceptionCatched(": discovery failed. " + message);
    }

}
