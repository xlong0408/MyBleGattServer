package xlong.gatt.server;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * Created by Administrator on 2017/4/14.
 */

public class MyProfile {

    private static final String TAG = MyProfile.class.getSimpleName();

    /* Current Time Service UUID */
//    public static UUID MY_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    public static UUID MY_SERVICE = UUID.fromString("0000fab1-0000-1000-8000-00805f9b34fb");

    /* Mandatory Current Time Information Characteristic */
//    public static UUID MY_CHARACTER    = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");
    public static UUID MY_CHARACTER    = UUID.fromString("0000fab2-0000-1000-8000-00805f9b34fb");

    /* Mandatory Client Characteristic Config Descriptor */
//    public static UUID MY_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static UUID MY_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    /**
     * Return a configured {@link BluetoothGattService} instance for the
     * Current Time Service.
     */
    public static BluetoothGattService createTimeService() {
        BluetoothGattService service = new BluetoothGattService(MY_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Current Time characteristic
        BluetoothGattCharacteristic currentTime = new BluetoothGattCharacteristic(MY_CHARACTER,
                //Read-only characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_WRITE |
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        BluetoothGattDescriptor configDescriptor = new BluetoothGattDescriptor(MY_CONFIG,
                //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);

        currentTime.addDescriptor(configDescriptor);

        service.addCharacteristic(currentTime);

        return service;
    }

}
