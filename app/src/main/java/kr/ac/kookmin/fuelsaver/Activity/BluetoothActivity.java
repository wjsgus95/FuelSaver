package kr.ac.kookmin.fuelsaver.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import kr.ac.kookmin.fuelsaver.R;

public class BluetoothActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        getActionBar().setTitle(R.string.bluetooth_activity_action_bar);


        if (!MainActivity.mBluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, MainActivity.REQUEST_BLUETOOTH_ENABLE);
        }
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
}
