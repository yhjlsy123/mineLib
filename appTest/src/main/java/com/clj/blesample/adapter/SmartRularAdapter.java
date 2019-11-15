package com.clj.blesample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clj.blesample.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Autor:Administrator
 * CreatedTime:2019/11/14 0014
 * UpdateTime:2019/11/14 0014 14:23
 * Des:设备列表
 * UpdateContent:
 **/
public class SmartRularAdapter extends BaseAdapter {
    private Context context;

    private List<BleDevice> dData;
    private ViewHolder viewHolder;


    public SmartRularAdapter(Context context, List<BleDevice> dData) {
        this.context = context;
        if (null == this.dData && null != dData) {
            this.dData = dData;
        } else {
            this.dData.clear();
            this.dData.addAll(dData);
        }


    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/14 0014
     * UpdateTime:2019/11/14 0014 14:11
     * Des:只显示已经成功连接的设备
     * UpdateContent:
     **/
    public void addDevice(BleDevice device) {
        if (null != dData && BleManager.getInstance().isConnected(device)) {
            dData.add(device);

        } else if (BleManager.getInstance().isConnected(device)) {
            dData = new ArrayList<BleDevice>();
            dData.add(device);
        }
        notifyDataSetChanged();
    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/14 0014
     * UpdateTime:2019/11/14 0014 14:13
     * Des:清楚所有设备
     * UpdateContent:
     **/
    public void clearDevice() {
        if (null != dData) {
            dData.clear();
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {
        return dData.size();
    }

    @Override
    public BleDevice getItem(int position) {
        return dData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_device, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mEtName.setText(dData.get(position).getName());
        return convertView;
    }

    static
    class ViewHolder {
        private ImageView mDevice;
        private TextView mEtName;

        ViewHolder(View view) {
            mDevice = view.findViewById(R.id.device);
            mEtName = view.findViewById(R.id.et_name);
        }
    }
}
