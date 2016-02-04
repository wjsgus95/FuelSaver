package kr.ac.kookmin.fuelsaver.Activity;

/*
public class BluetoothActivity extends ListActivity {

    private Switch mSwitch;
    private TextView mTextView;
    private ListView mListView;
    private Button mButton;

    private Set<BluetoothDevice> pairedDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        getActionBar().setTitle(R.string.bluetooth_activity_action_bar);


        mSwitch = (Switch) findViewById(R.id.bluetooth_switch);
        mTextView = (TextView) findViewById(R.id.bluetooth_text);
        mListView = (ListView) findViewById(R.id.paired_device_list);
        mButton = (Button) findViewById(R.id.bluetooth_button);


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
       /* if (requestCode == MainActivity.REQUEST_BLUETOOTH_ENABLE) {
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
*/
