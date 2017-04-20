package xlong.gatt.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.GATT;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    /* Bluetooth API */
    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    /* Collection of notification subscribers */
    private Set<BluetoothDevice> mRegisteredDevices = new HashSet<>();


    /**
     * Listens for Bluetooth adapter events to enable/disable
     * advertising and server functionality.
     */
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startAdvertising();
                    startServer();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopServer();
                    stopAdvertising();
                    break;
                default:
                    // Do nothing
            }

        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(MainActivity.this);

            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            BluetoothDevice device = (BluetoothDevice) mRegisteredDevices.toArray()[position];
           TextView textView = (TextView) holder.itemView;
            textView.setText(device.getAddress() + " " + device.getName());
        }

        @Override
        public int getItemCount() {
            return mRegisteredDevices.size();
        }
    }


    private RecyclerView mListView;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (RecyclerView) findViewById(R.id.listView);
        mListView.setAdapter(new MyAdapter());
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        // We can't continue without proper Bluetooth support
        if (!checkBluetoothSupport(bluetoothAdapter)) {
            finish();
        }

        // Register for system Bluetooth events
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);

        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is currently disabled...enabling");
            bluetoothAdapter.enable();
        } else {
            Log.d(TAG, "Bluetooth enabled...starting services");
            startAdvertising();
            startServer();
        }
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                BluetoothGattCharacteristic characteristic = mBluetoothGattServer.getService(MyProfile.MY_SERVICE).getCharacteristic(MyProfile.MY_CHARACTER);
                characteristic.setValue("hello there".getBytes());
                int n = 0;
                for (BluetoothDevice device : mRegisteredDevices) {

                    n++;
                    mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
                    System.out.println("通知设备" + device.getAddress() + " " + device.getBondState());
                }
                System.out.println("通知了" + n + "个设备");
            }
        };
        timer.schedule(timerTask, 1000 * 60, 1000 * 30);
        initMessage();
    }
    private String mMessage;
    private String mTxt = "The first wave of #io17 sessions is now live! Start building your custom schedule and check back often for updates and additions." +
            "\"He did clarify that the reason he had made that statement was that in the event that anything did happen to him that he was in fact pledging his allegiance to God for protection." +
            "Mr Muhammad's father later told the Los Angeles Times that his son believed he was part of an ongoing war between whites and blacks and that \"a battle was about to take place\"." +
            "Environment Minister Josh Frydenberg said Australia would think about culling sharks or introducing other measures, such as drum line traps.In 2014, the state trialled a shark cull on seven " +
            "beaches using baited traps, but it proved controversial and was halted by an environmental regulator. More than 170 sharks were caught but none of them were great whites.Ms Brouwer's" +
            " uncle Steve Evans said relatives were \"terribly heartbroken\" by her death in the town of Esperance.Android Things makes developing connected embedded devices easy by providing the same " +
            "Android development tools, best-in-class Android framework, and Google APIs that make developers successful on mobile.Apps for embedded devices bring developers closer to hardware peripherals " +
            "and drivers than phones and tablets. In addition, embedded devices typically present a single app experience to users. This document goes over the major additions, omissions, and differences between core Android development and Android Things." +
            "Android Things supports graphical user interfaces using the same UI toolkit available to traditional Android applications. In graphical mode, the application window occupies the full real estate of the display. Android Things does not include the system status bar or navigation buttons, giving applications full control over the visual user experience." +
            "However, Android Things does not require a display. On devices where a graphical display is not present, activities are still a primary component of your Android Things app. This is because the framework delivers all input events to the foreground activity, which has focus. Your app cannot receive key events or motion events through any other application component, such as a service." +
            "Each release of Android Things bundles the latest stable version of Google Play Services, and requires at least version 10.0.0 of the client SDK. Android Things does not include the Google Play Store, which is responsible for automatically updating Play Services on the device. Because the Play Services version on the device is static, apps cannot target a client SDK greater than the version bundled with the target release.";

    private void initMessage() {
        BatteryManager batteryManager=(BatteryManager)getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        JsonObject object = new JsonObject();
        object.addProperty("battery", battery);
        object.addProperty("model", Build.MODEL);
        object.addProperty("man", Build.MANUFACTURER);
        object.addProperty("version", Build.VERSION.RELEASE);
        object.addProperty("name", "4Test");
        object.addProperty("txt", mTxt);

        mMessage = object.toString();
        Log.i(TAG, "message byte cnt " + mMessage.getBytes().length);
        StringBuilder sb = new StringBuilder();
        for (byte b : mMessage.getBytes()) {
            sb.append(b & 0xff).append(" ");
        }
        System.out.println(sb.toString());
    }


    @Override
    protected void onStop() {
        super.onStop();
//        unregisterReceiver(mTimeReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            stopServer();
            stopAdvertising();
        }

        unregisterReceiver(mBluetoothReceiver);
    }


    /**
     * Callback to receive information about the advertisement process.
     */
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };



    /**
     * Begin advertising over Bluetooth that this device is connectable
     * and supports the Current Time Service.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(new ParcelUuid(MyProfile.MY_SERVICE))
                .build();

        mBluetoothLeAdvertiser
                .startAdvertising(settings, data, mAdvertiseCallback);
    }

    /**
     * Stop Bluetooth advertisements.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) return;

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    /**
     * Callback to handle incoming requests to the GATT server.
     * All read/write requests for characteristics and descriptors are handled here.
     */
    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(final BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device + "(" + device.getName() + ")");
                mRegisteredDevices.add(device);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MyAdapter)mListView.getAdapter()).notifyDataSetChanged();

                    }
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device + "(" + device.getName() + ")");
                //Remove device from any active subscriptions
                mRegisteredDevices.remove(device);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((MyAdapter)mListView.getAdapter()).notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.i(TAG, "onServiceAdded: status " + status + " service " + service.getUuid());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.i(TAG, "onCharacteristicWriteRequest offset " + offset);
            String m = Build.MANUFACTURER;
            if (mMessage.getBytes().length > 20) {

                int n;//需要发送几次
                int r = mMessage.getBytes().length % 20;
                if (r == 0) {
                    n = mMessage.getBytes().length / 20;
                } else {
                    n = mMessage.getBytes().length / 20 + 1;
                }
                for (int i=0; i<n; i++) {
                    int off = i * 20;
                    int len;
                    if (i < n-1) {
                        len = 20;
                    } else {
                        len = mMessage.getBytes().length - off;
                    }
                    characteristic.setValue(getSend20bytes(off, len));
                    mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
                }
            } else {
                characteristic.setValue(mMessage.getBytes());
                mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
            }

            mBluetoothGattServer.sendResponse(device, requestId, 0, offset, null);

        }
        //得到本次要发送的字节
        private byte[] getSend20bytes(int offset, int len) {
            byte[] send = new byte[len];
            for (int i=0; i<len; i++){
                send[i] = mMessage.getBytes()[offset + i];
            }
            return send;
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicReadRequest, offset " + offset);
            String model = Build.MODEL;
            String man = Build.MANUFACTURER;
            int l = 22;
            int len = 0;
            if ((mMessage.getBytes().length - offset) >= l) {
                len = l;
            } else {
                len = mMessage.getBytes().length - offset;
            }

            byte[] send = new byte[len];
            int n = 0;
            int k = offset;
            while (n < len){
               send[n] = mMessage.getBytes()[k];
               n++;
               k++;
            }
            mBluetoothGattServer.sendResponse(device, requestId, 0, 0, send);

        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
                                            BluetoothGattDescriptor descriptor) {
            Log.i(TAG, "onDescriptorReadRequest");
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded,
                                             int offset, byte[] value) {
            Log.i(TAG, "onDescriptorWriteRequest");
        }
    };


    /**
     * Initialize the GATT server instance with the services/characteristics
     * from the Time Profile.
     */
    private void startServer() {
        mBluetoothGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        if (mBluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }

        mBluetoothGattServer.addService(MyProfile.createTimeService());

        // Initialize the local UI
    }

    /**
     * Shut down the GATT server.
     */
    private void stopServer() {
        if (mBluetoothGattServer == null) return;

        mBluetoothGattServer.close();
    }

    private boolean checkBluetoothSupport(BluetoothAdapter bluetoothAdapter) {

        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }
}
