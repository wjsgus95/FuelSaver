/*package kr.ac.kookmin.fuelsaver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import java.io.InputStream;
import java.io.OutputStream;

import kr.ac.kookmin.fuelsaver.Activity.MainActivity;

/**
 * Created by HyunJun on 2/4/2016.
 */

/*
public class BluetoothManager {

    public BluetoothAdapter mBluetoothAdapter = null;
    public BluetoothDevice mBluetoothDevice = null;
    public BluetoothSocket mmSocket = null;

    public InputStream mmInputStream = null;
    public OutputStream mmOutputStream = null;

    BluetoothManager() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null)
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.bluetooth_error_alert_title)
                    .setMessage(R.string.bluetooth_error_alert_message)
                    .setPositiveButton(R.string.bluetooth_error_alert_positive_message, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, MainActivity.REQUEST_BLUETOOTH_ENABLE);
        }
    }
}
*/