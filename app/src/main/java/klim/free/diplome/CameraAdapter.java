package klim.free.diplome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

@SuppressWarnings("ALL")
public class CameraAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity mActivity;
    private List<Camera> mCameraList;

    public CameraAdapter(Activity activity,List<Camera> cameraList){
        mActivity = activity;
        mCameraList = cameraList;
    }
    @Override
    public int getCount() {
        return mCameraList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCameraList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.camera_item_layout,null);
        }

        TextView ipTextView = convertView.findViewById(R.id.camera_ip);

        Camera camera = mCameraList.get(position);

        ipTextView.setText(camera.getIp());


        return convertView;
    }
}