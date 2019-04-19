package klim.free.diplome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


@SuppressWarnings({"FieldCanBeLocal", "unused", "NullableProblems", "ConstantConditions"})
public class PresetsFragment extends Fragment implements View.OnClickListener, SimplePostTask.CallBack {

    private final static String TAG =  "TAG";

    private Button button1,button2,button3,button4,button5,
                   button6,button7,button8,button9,button10;

    private FloatingActionButton mFab;


    public PresetsFragment() {
        // Required empty public constructor
    }


    public static PresetsFragment newInstance(String param1, String param2) {
        PresetsFragment fragment = new PresetsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"presetsFragment onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"presetsFragment onCreateView");
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_presets, container, false);

        final SimplePostTask.CallBack callBack = this;

        mFab = view.findViewById(R.id.floatingActionButton);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("Choose number to set preset");
                String[] types = {"1", "2","3","4","5","6","7","8","9","10"};
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        new SimplePostTask(callBack).execute("SetPreset?PresetNumber=" + (which + 81));

                        Toast.makeText(getActivity(), "Preset saved at number " + (which + 1),
                                Toast.LENGTH_LONG).show();

                    }

                });

                b.show();
            }
        });

        button1 = view.findViewById(R.id.button);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        button4 = view.findViewById(R.id.button4);
        button5 = view.findViewById(R.id.button5);
        button6 = view.findViewById(R.id.button6);
        button7 = view.findViewById(R.id.button7);
        button8 = view.findViewById(R.id.button8);
        button9 = view.findViewById(R.id.button9);
        button10 = view.findViewById(R.id.button10);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        button10.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG,"presetsFragment onResume");
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Button butt = (Button) v;
        // button num + 80 = request parameter
        // redo
        int presetNum = Integer.valueOf(String.valueOf(butt.getText()));

        new SimplePostTask(this).execute("GotoPreset?PresetNumber=" + (presetNum + 80));
    }

    @Override
    public void exceptionCatched(final String message) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Unable to perform request" + message,
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (NullPointerException e) {
            Log.d(TAG,"FUCK");
        }

    }
}
