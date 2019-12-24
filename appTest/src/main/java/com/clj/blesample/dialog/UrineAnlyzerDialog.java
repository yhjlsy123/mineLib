package com.clj.blesample.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clj.blesample.R;
import com.clj.blesample.adapter.SmartRularAdapter;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.service.BluetoothDeviceService;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Autor:Administrator
 * CreatedTime:2019/11/13 0013
 * UpdateTime:2019/11/13 0013 11:28
 * Des:体温计数据读取
 * UpdateContent:
 **/
public class UrineAnlyzerDialog extends Dialog {
    private TextView mTitle;
    private GridView mGrid;
    private Button mRead;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    private OnClick onClick;
    private ReciveDeviceMessage rdm;
    private List<BleDevice> dData = new ArrayList<BleDevice>();
    private SmartRularAdapter adapter;
    private String mac;

    public UrineAnlyzerDialog(@NonNull Context context) {
        super(context);

    }

    public UrineAnlyzerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_dialog);
        mTitle = findViewById(R.id.title);
        mGrid = findViewById(R.id.grid);
        mRead = findViewById(R.id.read);
        mTitle.setText("智能蓝牙设备");
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.clj.fastble.service.action.bluetoosh.BAZ");
        rdm = new ReciveDeviceMessage();
        getContext().getApplicationContext().registerReceiver(rdm, filter);
        BluetoothDeviceService.startActionBaz(getContext(), null, null);
        adapter = new SmartRularAdapter(getContext(), dData);
        mGrid.setAdapter(adapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mac = adapter.getItem(position).getMac();
                adapter.clearDevice();
                startService();

            }
        });
        mRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mac = null;
                startService();
            }
        });


    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/19 0019
     * UpdateTime:2019/11/19 0019 16:32
     * Des:启动通信服务
     * UpdateContent:
     **/
    private void startService() {
        if (TextUtils.isEmpty(mac)) {
            BluetoothDeviceService.startActionBaz(getContext(), new String[]{"ZS"}, null);
        } else {
            BluetoothDeviceService.startActionBaz(getContext(), null, mac);
        }

    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/14 0014
     * UpdateTime:2019/11/14 0014 13:46
     * Des:视图点击事件回调
     * UpdateContent:
     **/
    public interface OnClick {
        public void click(int id);

        public void getResult(String result);
    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/14 0014
     * UpdateTime:2019/11/14 0014 14:28
     * Des:监听设备状态信息
     * UpdateContent:
     **/
    private class ReciveDeviceMessage extends BroadcastReceiver {
        public ReciveDeviceMessage() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                switch (intent.getIntExtra("d_service_status", 0)) {
                    case 0:
                        mRead.setText("启动搜索...");
                        break;
                    case 1:
                        mRead.setText("搜索完成");
                        startService();
                        mRead.setText("可用设备" + adapter.getCount() + "台");
                        break;

                    case 2:
                        BleDevice bd = (BleDevice) intent.getParcelableExtra("d_service_data");
                        if (null != bd && null != adapter) {
                            adapter.addDevice(bd);
                        }
                        break;

                    case 3:
                        startService();
                     /*   AlertDialog.Builder builder = new AlertDialog.Builder(ThermometerRular.this.getContext());
                        builder.setMessage("设备自动关闭断开连接,是否重新开启连接设备？");
                        builder.setNegativeButton("取消", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("确定", new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BluetoothDeviceService.startActionBaz(getContext(), "Bi", "");
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();*/
                        break;
                    case 4:
                        String strResult = HexUtil.getStrResult(intent.getByteArrayExtra("d_service_res_data"), true);

                        onClick.getResult(HexUtil.getStrResult(intent.getByteArrayExtra("d_service_res_data"), true));
                        break;

                }
            }

        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mac = null;
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(rdm);
    }


}
