package com.clj.blesample.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clj.blesample.R;
import com.clj.blesample.adapter.SmartRularAdapter;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.service.BluetoothDeviceService;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Autor:Administrator
 * CreatedTime:2019/11/13 0013
 * UpdateTime:2019/11/13 0013 11:28
 * Des:智能腰尺数据读取
 * UpdateContent:
 **/
public class SmartRular extends Dialog {
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

    public SmartRular(@NonNull Context context) {
        super(context);

    }

    public SmartRular(@NonNull Context context, int themeResId) {
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
        BluetoothDeviceService.startActionBaz(getContext(), "", "");
        adapter = new SmartRularAdapter(getContext(), dData);
        mGrid.setAdapter(adapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BleManager.getInstance().connect(adapter.getItem(position), new BleGattCallback() {
                    @Override
                    public void onStartConnect() {
                    }

                    @Override
                    public void onConnectFail(BleDevice bleDevice, BleException exception) {

                    }

                    @Override
                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        final List<BluetoothGattService> gServicelist = gatt.getServices();
                        if (gServicelist.size() == 4 && (gServicelist.get(2).getCharacteristics().get(0).getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            BleManager.getInstance().notify(
                                    bleDevice,
                                    gServicelist.get(2).getCharacteristics().get(0).getService().getUuid().toString(),
                                    gServicelist.get(2).getCharacteristics().get(0).getUuid().toString(),
                                    new BleNotifyCallback() {

                                        @Override
                                        public void onNotifySuccess() {
                                            Log.d("lsy", "onNotifySuccess");
                                            mRead.setText("读取准备就绪");
                                        }

                                        @Override
                                        public void onNotifyFailure(final BleException exception) {
                                            Log.d("lsy", exception.getDescription());
                                            mRead.setText("请确定设备是否正常开启");
                                            BluetoothDeviceService.startActionBaz(getContext(), "", "");

                                        }

                                        @Override
                                        public void onCharacteristicChanged(byte[] data) {
                                            Log.d("lsy", HexUtil.getResult(data, true) + "");
                                            onClick.getResult(HexUtil.getResult(data, true) + "");

                                        }
                                    });
                        }


                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {


                    }
                });
            }
        });


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
            Log.d("SmartRular", ">>>>>>");
            if (null != intent) {
                switch (intent.getIntExtra("d_service_status", 0)) {
                    case 0:
                        mRead.setText("启动搜索...");
                        break;
                    case 1:
                        mRead.setText("搜索完成");
                        break;

                    case 2:
                        BleDevice bd = (BleDevice) intent.getParcelableExtra("d_service_data");
                        if (null != bd && null != adapter) {
                            adapter.addDevice(bd);
                            adapter.notifyDataSetChanged();
                            mRead.setText("可用设备" + adapter.getCount() + "台");
                        }
                        break;

                    case 3:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("设备自动关闭断开连接,是否重新开启连接设备？");
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BluetoothDeviceService.startActionBaz(getContext(), "", "");
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();


                        break;

                }
            }

        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(rdm);
    }


/*    final List<BluetoothGattService> gServicelist = gatt.getServices();
                        if (gServicelist.size() == 4 && (gServicelist.get(2).getCharacteristics().get(0).getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
        BleManager.getInstance().notify(
                bleDevice,
                gServicelist.get(2).getCharacteristics().get(0).getService().getUuid().toString(),
                gServicelist.get(2).getCharacteristics().get(0).getUuid().toString(),
                new BleNotifyCallback() {

                    @Override
                    public void onNotifySuccess() {
                        Log.d("lsy", "onNotifySuccess");
                        img_loading.clearAnimation();
                        img_loading.setVisibility(View.INVISIBLE);
                        btn_scan.setText(getString(R.string.start_scan));
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        Log.d("lsy", exception.getDescription());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.d("lsy", HexUtil.getResult(gServicelist.get(2).getCharacteristics().get(0).getValue(), true) + "");
                    }
                });
    }*/
}
