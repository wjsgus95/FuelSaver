package kr.ac.kookmin.fuelsaver.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import kr.ac.kookmin.fuelsaver.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    static GoogleApiClient mGoogleApiClient;

    public static final int REQUEST_GOOGLE_SIGNIN = 1000;
    public static final int REQUEST_BLUETOOTH_ENABLE = 1100;
    public static final int REQUEST_CONNECT_DEVICE = 1200;

    private static final String TAG = "MainActivity";
    private static final String serverClientID = "198378412781-e2p9u5i1hhu3h65t7aqrk1kndiomflf0.apps.googleusercontent.com";

    private static String userIdToken = null;
    private static String userName = null;
    private static String userEmail = null;

    private TextView userNameText, userEmailText;

    private static TextView connectedDeviceText;

    private static TextView rpmText;
    private static TextView speedText;
    private static TextView massAirFlowText;
    private static TextView throttlePositionText;
    private static TextView engineCoolantTemperatureText;
    private static TextView engineLoadText;

    private SignInButton signInButton;
    private MenuItem signOutItem;

    private boolean isStartData = true;
    public static boolean runWorkerThread = false;

    public static String deviceAddress = null;

    public Thread workerThread = null;

    public static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothDevice mBluetoothDevice = null;
    public static BluetoothSocket mmSocket = null;

    public static InputStream mmInputStream = null;
    public static OutputStream mmOutputStream = null;

    public static String rpm = null;
    public static String speed = null;
    public static String mass_air_flow = null;
    public static String throttle_position = null;
    public static String engine_coolant_temperature = null;
    public static String engine_load = null;

    public static Handler myHandler = new Handler();

    public Intent connectIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        userNameText = (TextView) header.findViewById(R.id.userNameText);
        userEmailText = (TextView) header.findViewById(R.id.userEmailText);

        connectedDeviceText = (TextView) findViewById(R.id.connectedDeviceText);

        rpmText = (TextView) findViewById(R.id.rpmText);
        speedText = (TextView) findViewById(R.id.speedText);
        massAirFlowText = (TextView) findViewById(R.id.massAirFlowText);
        throttlePositionText = (TextView) findViewById(R.id.throttlePositionText);
        engineCoolantTemperatureText = (TextView) findViewById(R.id.engineCoolantTemperatureText);
        engineLoadText = (TextView) findViewById(R.id.engineLoadText);

        rpmText.setVisibility(View.INVISIBLE);
        speedText.setVisibility(View.INVISIBLE);
        massAirFlowText.setVisibility(View.INVISIBLE);
        throttlePositionText.setVisibility(View.INVISIBLE);
        engineCoolantTemperatureText.setVisibility(View.INVISIBLE);
        engineLoadText.setVisibility(View.INVISIBLE);

        signInButton = (SignInButton) header.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        signOutItem = navigationView.getMenu().findItem(R.id.nav_signout);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
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
            startActivityForResult(enableBT, REQUEST_BLUETOOTH_ENABLE);
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientID)
                .requestEmail()
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this   /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGNIN);

        workerThread = new Thread(new MyRunnable());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_options) {
            return true;
        }

        if(id == R.id.action_start_live_data) {
            if(isStartData) {
                item.setTitle(R.string.action_stop_live_data);
                runWorkerThread = true;
                connectDevice(connectIntent);
            }

            else {
                item.setTitle(R.string.action_start_live_data);
                runWorkerThread = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bluetooth) {

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_BLUETOOTH_ENABLE);
            }

            // Handle on bluetooth action
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_signout) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            // TODO Handle Anonymous User
            userNameText.setText(R.string.sign_in_required);
            userEmailText.setText(R.string.app_name);

            signInButton.setVisibility(SignInButton.VISIBLE);

            signOutItem.setVisible(false);
            this.invalidateOptionsMenu(); // Adjusts the Action Bar Accordingly
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_BLUETOOTH_ENABLE:
                if (resultCode == Activity.RESULT_CANCELED) // if user declines
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
                break;

            case REQUEST_GOOGLE_SIGNIN:
                boolean isSuccess = Auth.GoogleSignInApi.getSignInResultFromIntent(data).isSuccess();

                if (isSuccess) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);

                    userNameText.setText(userName);
                    userEmailText.setText(userEmail);

                    signInButton.setVisibility(SignInButton.INVISIBLE);

                    signOutItem.setVisible(true);
                    this.invalidateOptionsMenu(); // Adjusts the Action Bar Accordingly
                } else {
                    // TODO Handle Anonymous User
                    userNameText.setText(R.string.sign_in_required);
                    userEmailText.setText(R.string.app_name);

                    signInButton.setVisibility(SignInButton.VISIBLE);

                    signOutItem.setVisible(false);
                    this.invalidateOptionsMenu(); // Adjusts the Action Bar Accordingly
                }
                break;

            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectIntent = data;

                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    // Get the BluetoothDevice object
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);

                    connectedDeviceText.setText("Connected To : " + mBluetoothDevice.getName());
                }


        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "Sign in Successful");
            Log.d(TAG, "ID Token : " + acct.getIdToken());
            Log.d(TAG, "Email : " + acct.getEmail());
            Log.d(TAG, "Name : " + acct.getDisplayName());

            userIdToken = acct.getIdToken();
            userEmail = acct.getEmail();
            userName = acct.getDisplayName();
        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "Sign in Unsuccessful");
        }
    }

    @Override
    public void onClick(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGNIN);
    }

    /**
     * Establish connection with other device
     *
     * @param data An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     */
    private void connectDevice(Intent data) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        // Get the BluetoothDevice object
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);

        // Attempt to connect to the device
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        try {
            mmSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        rpmText.setVisibility(View.VISIBLE);
        speedText.setVisibility(View.VISIBLE);
        massAirFlowText.setVisibility(View.VISIBLE);
        throttlePositionText.setVisibility(View.VISIBLE);
        engineCoolantTemperatureText.setVisibility(View.VISIBLE);
        engineLoadText.setVisibility(View.VISIBLE);

        workerThread.start();
    }


    public static class MyRunnable implements Runnable {

        public void run() {

            if(!runWorkerThread)
                return;

            while (true) {
                //Do work
                try {
                    new EchoOffCommand().run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    new LineFeedOffCommand().run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    new TimeoutCommand(700).run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    new AmbientAirTemperatureCommand().run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                } catch (Exception e) {
                    //  Handle Errors
                }

                RPMCommand engineRpmCommand = new RPMCommand();
                SpeedCommand speedCommand = new SpeedCommand();
                MassAirFlowCommand airflowCommand = new MassAirFlowCommand();
                ThrottlePositionCommand throttleCommand = new ThrottlePositionCommand();
                EngineCoolantTemperatureCommand temperatureCommand = new EngineCoolantTemperatureCommand();
                LoadCommand loadCommand = new LoadCommand();

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        engineRpmCommand.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        speedCommand.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        airflowCommand.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        throttleCommand.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        temperatureCommand.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                        loadCommand.run(mmSocket.getInputStream(), mmSocket.getOutputStream());
                    } catch (Exception e) {
                    }
                    // TODO handle commands result

                    Log.i("Info", "RPM : " + engineRpmCommand.getFormattedResult());
                    Log.i("Info", "Speed : " + speedCommand.getFormattedResult());
                    Log.i("Info", "MassAirFlow : " + airflowCommand.getFormattedResult());
                    Log.i("Info", "Throttle Position : " + throttleCommand.getFormattedResult());
                    Log.i("Info", "Engine Coolant Temperature : " + temperatureCommand.getFormattedResult());
                    Log.i("Info", "Engine Load : " + loadCommand.getFormattedResult());

                    /*

                     */

                    rpm = engineRpmCommand.getFormattedResult();
                    speed = speedCommand.getFormattedResult();
                    mass_air_flow = airflowCommand.getFormattedResult();
                    throttle_position = throttleCommand.getFormattedResult();
                    engine_coolant_temperature = temperatureCommand.getFormattedResult();
                    engine_load = loadCommand.getFormattedResult();

                    //if(upload.getStatus() == AsyncTask.Status.FINISHED)
                    //upload.execute();

                    myHandler.post(new Runnable() { // Handler Provides Second-Handed Access To UI Thread
                        public void run() {
                            rpmText.setText("Rev : " + rpm);
                            speedText.setText("Speed : " + speed);
                            massAirFlowText.setText("Mass Air Flow : " + mass_air_flow);
                            throttlePositionText.setText("Throttle Position : " + throttle_position);
                            engineCoolantTemperatureText.setText("Engine Coolant Temperature " + engine_coolant_temperature);
                            engineLoadText.setText("Engine Load : " + engine_load);
                        }
                    });

                    try {
                        HttpClient client = new DefaultHttpClient();


                        String postURL = "http://211.192.66.212:8080/index.jsp";


                        HttpPost post = new HttpPost(postURL);

                        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("rpm", rpm));
                        params.add(new BasicNameValuePair("speed", speed));
                        params.add(new BasicNameValuePair("mass_air_flow", mass_air_flow));
                        params.add(new BasicNameValuePair("throttle_position", throttle_position));
                        params.add(new BasicNameValuePair("engine_coolant_temperature", engine_coolant_temperature));
                        params.add(new BasicNameValuePair("engine_load", engine_load));


                        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                        post.setEntity(ent);

                        if(rpm != null) {
                            HttpResponse responsePOST = client.execute(post);
                            HttpEntity resEntity = responsePOST.getEntity();
                            if (resEntity != null) {
                                Log.i("RESPONSE", EntityUtils.toString(resEntity));
                            }
                        }

                        params = null;  // just in case

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }


            }
        }
    }
}
