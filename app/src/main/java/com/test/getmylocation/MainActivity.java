package com.test.getmylocation;

import android.app.Dialog;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    public static final int ERROR_DIALOG_REQUEST_CODE = 1000;
    private static final String TAG = "DEBUG_MYLOC";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private TextView locationCoordinates;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationCoordinates = (TextView) findViewById(R.id.textView2);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });

        if(isPlayServicesAvailable()){
            buildGoogleApiClient();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
        else{
            Log.d(TAG,"OnStart Error");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    public boolean isPlayServicesAvailable(){

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG,"Services Available");
            return true;
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError(available)){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(available,this, ERROR_DIALOG_REQUEST_CODE);
            dialog.show();
        }
        else{
            Log.d(TAG,"Services Ain't Available");
        }
        return false;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG,"Connected to Play Services");
    }

    private void displayLocation() {

        if(mLastLocation != null){
            double latitude= mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            locationCoordinates.setText(latitude+" , "+longitude);
        }
        else{
            Toast.makeText(this,"Can't Locate",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }


}
