package kr.ac.kookmin.fuelsaver.Activity;


import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Set;

import kr.ac.kookmin.fuelsaver.R;

public class BluetoothActivity extends ListActivity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    public ArrayAdapter<String> pairedDevicesArrayAdapter =
            new ArrayAdapter<String>(this, R.layout.device_name);
    public ArrayAdapter<String> newDevicesArrayAdapter =
            new ArrayAdapter<String>(this, R.layout.device_name);

    private TextView pairedDevicesText;
    private TextView newDevicesText;

    private ListView pairedDevicesList;
    private ListView newDevicesList;

    private Button scanButton;

    private Set<BluetoothDevice> pairedDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        getActionBar().setTitle(R.string.bluetooth_activity_action_bar);


        pairedDevicesText = (TextView) findViewById(R.id.title_paired_devices);
        newDevicesText = (TextView) findViewById(R.id.title_new_devices);

        pairedDevicesList = (ListView) findViewById(R.id.paired_devices);
        newDevicesList = (ListView) findViewById(R.id.new_devices);
        scanButton = (Button) findViewById(R.id.button_scan);


        if (!MainActivity.mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, MainActivity.REQUEST_BLUETOOTH_ENABLE);
        }

        Set<BluetoothDevice> pairedDevices = MainActivity.mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        pairedDevicesList.setAdapter(pairedDevicesArrayAdapter);
        pairedDevicesList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pairedDevicesList.setOnItemClickListener(mDeviceClickListener);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
            resultCode == 0 if user declines to turn on Bluetooth
            resultCode == -1 if user accepts to turn on Bluetooth
         */
        if (requestCode == MainActivity.REQUEST_BLUETOOTH_ENABLE) {
            if (resultCode == 0) // if user declines
            {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.bluetooth_warning_alert_title)
                        .setMessage(R.string.bluetooth_warning_alert_message)
                        .setPositiveButton(R.string.bluetooth_warning_alert_positive_message, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            // MainActivity.mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            MainActivity.deviceAddress = address;

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

}

