package klim.free.diplome;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

@SuppressWarnings("FieldCanBeLocal")
public class SelectServerActivity extends AppCompatActivity {

    private EditText mIpEditText, mPortEditText;
    private FloatingActionButton mSaveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server);

        mIpEditText = findViewById(R.id.serverIP);
        mPortEditText = findViewById(R.id.serverPort);

        mSaveData = findViewById(R.id.saveServerAndPortButton);

        mSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = mIpEditText.getText().toString();
                String port = mPortEditText.getText().toString();

                Intent intent = new Intent(getBaseContext(),MainActivity.class);

                intent.putExtra("IP",ip);
                intent.putExtra("Port",port);

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String IP = extras.getString("IP");
            String Port = extras.getString("Port");

            Log.d("TAG","retrieved " + IP + "," + Port);

            mIpEditText.setText(IP);
            mPortEditText.setText(Port);
        }

    }
}
