package com.clj.fastble.service;

import android.app.IntentService;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BluetoothDeviceService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.clj.fastble.service.action.bluetooth.FOO";
    private static final String ACTION_BAZ = "com.clj.fastble.service.action.bluetoosh.BAZ";
    public static final String ACTION_BRADCAST = "com.clj.fastble.service.action.bluetoosh.ReciveDeviceMessage";


    // TODO: Rename parameters
    private static final String EXTRA_BLUETOOEHNAME = "com.clj.fastble.service.extra.PARAM1";
    private static final String EXTRA_BLUTOOTHMAC = "com.clj.fastble.service.extra.PARAM2";

    private List<BluetoothGatt> listGatt = new ArrayList<BluetoothGatt>();

    public BluetoothDeviceService() {
        super("BluetoothDeviceService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, BluetoothDeviceService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_BLUETOOEHNAME, param1);
        intent.putExtra(EXTRA_BLUTOOTHMAC, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String[] param1, String param2) {
        Intent intent = new Intent(context, BluetoothDeviceService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_BLUETOOEHNAME, param1);
        intent.putExtra(EXTRA_BLUTOOTHMAC, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_BLUETOOEHNAME);
                final String param2 = intent.getStringExtra(EXTRA_BLUTOOTHMAC);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String[] param1 = intent.getStringArrayExtra(EXTRA_BLUETOOEHNAME);
                final String param2 = intent.getStringExtra(EXTRA_BLUTOOTHMAC);
                handleActionBaz(param1, param2);


            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo


    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String[] param1, String param2) {
        // TODO: Handle action Baz
        setScanRule(param2, param1);
        startScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/19 0019
     * UpdateTime:2019/11/19 0019 13:09
     * Des:搜索方式mac 地址和蓝牙名称
     * UpdateContent:
     **/
    private void setScanRule(String mac, String[] names) {
        String[] uuids = null;
        UUID[] serviceUuids = null;
        if (uuids != null && uuids.length > 0) {
            serviceUuids = new UUID[uuids.length];
            for (int i = 0; i < uuids.length; i++) {
                String name = uuids[i];
                String[] components = name.split("-");
                if (components.length != 5) {
                    serviceUuids[i] = null;
                } else {
                    serviceUuids[i] = UUID.fromString(uuids[i]);
                }
            }
        }

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, names)   // 只扫描指定广播名的设备，可选
                .setDeviceMac(mac)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /**
     * Autor:Administrator
     * CreatedTime:2019/11/12 0012
     * UpdateTime:2019/11/12 0012 14:23
     * Des:
     * UpdateContent:
     **/
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                Intent intent = new Intent(ACTION_BAZ);
                intent.putExtra("d_service_status", 0);
                intent.setPackage(getApplication().getPackageName());
                sendBroadcast(intent);

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.d("lsy", bleDevice.getName() + "onScanning");
//                 && bleDevice.getName().contains(bName)
                if (!(TextUtils.isEmpty(bleDevice.getName()))) {
                    BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                        @Override
                        public void onStartConnect() {
                            Log.d("lsy", "onStartConnect");
                        }

                        @Override
                        public void onConnectFail(BleDevice bleDevice, BleException exception) {

                            Log.d("lsy", exception.getDescription() + "onConnectFail");

                        }

                        @Override
                        public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                List<BluetoothGattService> gservice = gatt.getServices();
                                listGatt.add(gatt);
                                Intent intent = new Intent(ACTION_BAZ);
                                intent.putExtra("d_service_status", 2);
                                intent.putExtra("d_service_data", bleDevice);
                                intent.setPackage(getApplication().getPackageName());
                                sendBroadcast(intent);

                                if (null != gservice) {
                                    for (BluetoothGattService gg : gatt.getServices()) {
                                        List<BluetoothGattCharacteristic> chL = gg.getCharacteristics();
                                        for (BluetoothGattCharacteristic ich : chL) {
                                            if ((ich.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                                if (null != ich.getService() && null != ich.getService().getUuid() && null != ich.getUuid()) {

                                                    BleManager.getInstance().notify(bleDevice, ich.getService().getUuid().toString(), ich.getUuid().toString(), new BleNotifyCallback() {
                                                        @Override
                                                        public void onNotifySuccess() {
                                                            Log.d("lsy", "onNotifySuccess");

                                                        }

                                                        @Override
                                                        public void onNotifyFailure(BleException exception) {

                                                        }

                                                        @Override
                                                        public void onCharacteristicChanged(byte[] data) {
                                                            Intent intent = new Intent(ACTION_BAZ);
                                                            intent.putExtra("d_service_status", 4);
                                                            intent.putExtra("d_service_res_data", data);
                                                            intent.setPackage(getApplication().getPackageName());
                                                            sendBroadcast(intent);

                                                        }
                                                    });


                                                }

                                            }
                                        }

                                    }
                                }

                            }


                        }

                        @Override
                        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                            listGatt.remove(gatt);
                            Intent intent = new Intent(ACTION_BAZ);
                            intent.putExtra("d_service_status", 3);
                            intent.putExtra("d_service_data", device);
                            intent.setPackage(getApplication().getPackageName());
                            sendBroadcast(intent);
                        }
                    });

                }


            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Intent intent = new Intent(ACTION_BAZ);
                intent.putExtra("d_service_status", 1);
                intent.setPackage(getApplication().getPackageName());
                sendBroadcast(intent);

            }
        });
    }


}
