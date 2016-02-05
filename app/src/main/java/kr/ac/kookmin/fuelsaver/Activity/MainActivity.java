package kr.ac.kookmin.fuelsaver.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.InputStream;
import java.io.OutputStream;

import kr.ac.kookmin.fuelsaver.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    static GoogleApiClient mGoogleApiClient;

    public static final int REQUEST_GOOGLE_SIGNIN = 1000;
    public static final int REQUEST_BLUETOOTH_ENABLE = 1100;

    private static final String TAG = "MainActivity";
    private static final String serverClientID = "198378412781-e2p9u5i1hhu3h65t7aqrk1kndiomflf0.apps.googleusercontent.com";

    private static String userIdToken = null;
    private static String userName = null;
    private static String userEmail = null;

    private TextView userNameText, userEmailText;

    private SignInButton signInButton;
    private MenuItem signOutItem;

    public static String deviceAddress = null;


    public static BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothDevice mBluetoothDevice = null;
    public static BluetoothSocket mmSocket = null;

    public static InputStream mmInputStream = null;
    public static OutputStream mmOutputStream = null;


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

        signInButton = (SignInButton) header.findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        signOutItem = navigationView.getMenu().findItem(R.id.nav_signout);

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
                .enableAutoManage(this /* FragmentActivity */ , this   /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGNIN);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bluetooth) {
            // Handle on bluetooth action
            Intent intent = new Intent(this, BluetoothActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_signout) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            signOutItem.setVisible(false);
            this.invalidateOptionsMenu();
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

        /*
            resultCode == 0 if user declines to turn on Bluetooth
            resultCode == -1 if user accepts to turn on Bluetooth
         */
        if(requestCode == REQUEST_BLUETOOTH_ENABLE)
        {
            if(resultCode == 0) // if user declines
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


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_GOOGLE_SIGNIN) {
            boolean isSuccess = Auth.GoogleSignInApi.getSignInResultFromIntent(data).isSuccess();

            if(isSuccess) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);

                userNameText.setText(userName);
                userEmailText.setText(userEmail);

                signInButton.setVisibility(SignInButton.INVISIBLE);

                signOutItem.setVisible(true);
                this.invalidateOptionsMenu(); // Adjusts the Action Bar Accordingly
            }
            else {
                // TODO Handle Anonymous User
                userNameText.setText(R.string.sign_in_required);
                userEmailText.setText(R.string.app_name);

                signInButton.setVisibility(SignInButton.VISIBLE);

                signOutItem.setVisible(false);
                this.invalidateOptionsMenu(); // Adjusts the Action Bar Accordingly
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
}
