package com.androidtutorialpoint.glogin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener {

        private static final String TAG = MapsActivity.class.getSimpleName();

        private GoogleMap mMap;
        ArrayList<LatLng> MarkerPoints;
        GoogleApiClient mGoogleApiClient;
        Location mLastLocation;
        Marker mCurrLocationMarker;
        Marker bounceMarker;
        LocationRequest mLocationRequest;
        //https://stackoverflow.com/questions/46481789/android-locationservices-fusedlocationapi-deprecated
        FusedLocationProviderClient mFusedLocationClient;

        int PROXIMITY_RADIUS = 10000;
        double latitude, longitude;
        double end_latitude, end_longitude;

        public static final int REQUEST_LOCATION_CODE = 99;

        private final static int RC_PICK_CONTACT = 2;
        private final static int ALERT_INTERVAL = 1000000;// these are milisecs - NOTE


        private int messageType;
        private final int HELP_MESSAGE = 1;
        private final int LOCATION_MESSAGE = 2;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

            //Check if Google Play Services Available or not
            if (!CheckGooglePlayServices()) {
                Log.d("onCreate", "Google Play Services are not available.");
                finish();
            } else {
                Log.d("onCreate", "Google Play Services available.");
            }

            // Initializing
            MarkerPoints = new ArrayList<>();

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            ZoomControls zoom = (ZoomControls) findViewById(R.id.zcZoom);
            zoom.setOnZoomInClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                }
            });

            zoom.setOnZoomOutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                }
            });

            Log.i("MapActivity", "onCreate");


    }
//////
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    //ask for permission
    // https://www.youtube.com/watch?v=T2R9ETdd24Y
    // https://github.com/priyankapakhale/GoogleMapsNearbyPlacesDemo/blob/master/app/src/main/java/com/example/priyanka/mapsdemo/MapsActivity.java
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;

            //sms firebase
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                sendSMSAfterPermissionGranted();
            }
        }
    }


    private void sendSMSAfterPermissionGranted() {

        if (messageType != HELP_MESSAGE)
            return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mContactsDatabaseReference = databaseReference.child("emergency_contacts");

        Query contacts = mContactsDatabaseReference.orderByValue();

        contacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    StringTokenizer st = new StringTokenizer(child.getValue().toString(), "|");

                    //   Toast.makeText(MapsActivity.this, st.nextToken() + " " + st.nextToken(), Toast.LENGTH_LONG).show();

                    String name = st.nextToken();
                    String phone = st.nextToken();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, "HELP PLEASE", null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent",
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //https://stackoverflow.com/questions/31371865/replace-getmap-with-getmapasync
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

    public void onSearch(View view) {
        Object[] dataTransfer;
        final EditText location_tf = (EditText) findViewById(R.id.etLocationEntry);
        String location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);


            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            //TODO: ISSUE PART
//            dataTransfer = new Object[3];
//            GetDirectionsData getDirectionsData = new GetDirectionsData();
//            dataTransfer[0] = mMap;
//            dataTransfer[1] = getDirectionsUrl();
//            dataTransfer[2] = latLng;
//            getDirectionsData.execute(dataTransfer);

            //mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));

            float results[] = new float[2];
            Location.distanceBetween(latitude, longitude, address.getLatitude(), address.getLongitude(), results);
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.win)).position(latLng).title("New Goal").snippet(results[0] / 1000 + "KM"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            //mMap.clear();
//
            LatLng latLng1 = new LatLng(latitude, longitude);
            MarkerOptions run = new MarkerOptions();
            run.position(latLng1);
            run.draggable(true);
            run.title("One Step At A Time");
            run.icon(BitmapDescriptorFactory.fromResource(R.drawable.run));
            mCurrLocationMarker = mMap.addMarker(run);

//            Timer timer = new Timer();
//            TimerTask updateProfile = new CustomTimerTask(MapsActivity.this);
//            timer.scheduleAtFixedRate(updateProfile, 10,5000);
//            mCurrLocationMarker.showInfoWindow();
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1,18.0f));

        }

        //https://stackoverflow.com/questions/18331233/how-to-define-a-reset-button-to-clear-all-text-field-at-one-click
        Button reset = (Button) findViewById(R.id.btClear);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location_tf.setText(" ");

            }
        });
    }

    public void changeType(View view) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    //https://stackoverflow.com/questions/34139048/cannot-resolve-manifest-permission-access-fine-location
//    @Override
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //https://code.tutsplus.com/tutorials/getting-started-with-google-maps-for-android-basics--cms-24635
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);


    }

    @Override
    public void onLocationChanged(Location location) {

//        //ADDED
//        Object[] data = new Object[3];
//        //ADDED

        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.draggable(true);
//        markerOptions.title("On the Go");
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.run));
//        //Bitmap markerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.run);
//        //pulseMarker(markerOptions, markerOptions, 1000);
//        mCurrLocationMarker = mMap.addMarker(markerOptions);


        mCurrLocationMarker = mMap
                .addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("On the Go")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.run)).draggable(true));
        ////https://stackoverflow.com/questions/22523443/want-to-animate-marker-in-every-10-seconds-in-google-map

        run();
        mCurrLocationMarker.showInfoWindow();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18.0f));

//        //ADDED
//        GetDirectionsData getDirectionsData = new GetDirectionsData();
//        data[0] = mMap;
//        data[1] = getDirectionsUrl();
//        data[2] = new LatLng(end_latitude, end_longitude);
//        getDirectionsData.execute(data);
//        mMap.clear();
//        //ADDED

        //https://stackoverflow.com/questions/14244317/toast-background-color-being-changed

        //Create your Toast with whatever params you need
        Toast toast = Toast.makeText(MapsActivity.this, "Location Changed", Toast.LENGTH_SHORT);

//Set the background for the toast using android's default toast_frame.
//Optionally you can set the background color to #646464 which is the
//color of the frame
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.frame);

//Get the TextView for the toast message so you can customize
        TextView toastMessage = (TextView) view.findViewById(android.R.id.message);

//Set background color for the text.
        toastMessage.setTextColor(Color.BLUE);
        toast.show();

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }

    }

//    //https://stackoverflow.com/questions/40346054/how-to-animate-marker-on-google-maps
//    private void pulseMarker(final Bitmap markerIcon, final MarkerOptions markerOptions, final long onePulseDuration) {
//        final Handler handler = new Handler();
//        final long startTime = System.currentTimeMillis();
//
//        final CycleInterpolator interpolator = new CycleInterpolator(1f);
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = System.currentTimeMillis() - startTime;
//                float t = interpolator.getInterpolation((float) elapsed / onePulseDuration);
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(scaleBitmap(markerIcon, 1f + 0.05f * t)));
//                handler.postDelayed(this, 16);
//            }
//        });
//    }
//
//    public Bitmap scaleBitmap(Bitmap bitmap, float scaleFactor) {
//        final int sizeX = Math.round(bitmap.getWidth() * scaleFactor);
//        final int sizeY = Math.round(bitmap.getHeight() * scaleFactor);
//        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, false);
//        return bitmapResized;
//    }

    ////https://stackoverflow.com/questions/22523443/want-to-animate-marker-in-every-10-seconds-in-google-map
    Handler handler = new Handler();
    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        final long start = SystemClock.uptimeMillis();
                        final long duration = 3000;

                        final BounceInterpolator interpolator = new BounceInterpolator();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                long elapsed = SystemClock.uptimeMillis() - start;
                                float t = Math.max(
                                        1 - interpolator.getInterpolation((float) elapsed
                                                / duration), 0);
                                mCurrLocationMarker.setAnchor(0.5f, 0.1f+1*t);

                                if (t > 0.0) {
                                    // Post again 16ms later.
                                    handler.postDelayed(this, 16);
                                }
                            }
                        });
                    }
                });
            }
        }).start();

    }

    //https://gist.github.com/piruin/94dc141e7736851b002c
    private void startDropMarkerAnimation(final MarkerOptions markerOptions) {
        GoogleMap googleMap = mMap;
        final LatLng target = markerOptions.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point targetPoint = proj.toScreenLocation(target);
        final long duration = (long) (200 + (targetPoint.y * 0.6));
        Point startPoint = proj.toScreenLocation(markerOptions.getPosition());
        startPoint.y = 0;
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final LinearOutSlowInInterpolator interpolator = new LinearOutSlowInInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                markerOptions.position(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later == 60 frames per second
                    handler.postDelayed(this, 16);
                }
            }
        });}
    private void getNearbyPlaces(String type) {
        Object dataTransfer[] = new Object[2];
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        mMap.clear();
        String url = getUrl(latitude, longitude, type);

        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyPlacesData.execute(dataTransfer);
    }

    public void onClick(View v) {
        Object[] dataTransfer;

        switch (v.getId()) {
            case R.id.btSearch: {
                EditText tf_location = (EditText) findViewById(R.id.etLocationEntry);
                String location = tf_location.getText().toString();
                List<Address> addressList = null;
                MarkerOptions markerOptions = new MarkerOptions();
                Log.d("location = ", location);

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);


                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }

                    if (addressList != null) {
                        for (int i = 0; i < addressList.size(); i++) {
                            Address myAddress = addressList.get(i);
                            LatLng latLng = new LatLng(myAddress.getLatitude(), myAddress.getLongitude());
                            markerOptions.position(latLng);
                            mMap.addMarker(markerOptions);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                        }
                    }

                }
            }
            break;
            case R.id.B_hospital:
                getNearbyPlaces("hospital");
                Toast.makeText(MapsActivity.this, getResources().getString(R.string.show_hospitals), Toast.LENGTH_LONG).show();
                break;

            case R.id.B_restaurant:
                getNearbyPlaces("restaurant");
                Toast.makeText(MapsActivity.this, getResources().getString(R.string.show_restaurants), Toast.LENGTH_LONG).show();
                break;

            case R.id.B_school:
                getNearbyPlaces("school");
                Toast.makeText(MapsActivity.this, getResources().getString(R.string.show_schools), Toast.LENGTH_LONG).show();
                break;

            case R.id.B_to:
                dataTransfer = new Object[3];
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = getDirectionsUrl();
                dataTransfer[2] = new LatLng(end_latitude, end_longitude);
                getDirectionsData.execute(dataTransfer);

                mMap.clear();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(end_latitude, end_longitude));
                markerOptions.title("The Goal");
                markerOptions.draggable(true);
                float results[] = new float[2];
                Location.distanceBetween(latitude, longitude, end_latitude, end_longitude, results);
                markerOptions.snippet(results[0] / 1000 + "KM");
                mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));

                LatLng latLng = new LatLng(latitude, longitude);
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.draggable(true);
                markerOptions.title("One Step At A Time");
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.run));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //http://ramsandroid4all.blogspot.in/2013/01/alertdialog-in-android.html
                //start alertdialog handler
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAlertDialog();
                        // start another task
                        handler.postDelayed(this, ALERT_INTERVAL);
                    }
                }, ALERT_INTERVAL);

                break;

        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

        //SettingDialogTitle
        builder.setTitle("Checking...");
        builder.setMessage("Are you OK?").setPositiveButton("yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                dialog.dismiss();
                Toast.makeText(MapsActivity.this, "Good to know", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //https://stackoverflow.com/questions/8034823/android-phone-number-calling-function-in-an-activity
                //trigger sms sending to firebase
                messageType = HELP_MESSAGE;
                sendSMSMessage();

                Toast.makeText(MapsActivity.this, "We will be in touch with your emergency contacts and ring police NOW", Toast.LENGTH_LONG).show();
                Intent dial = new Intent();
                dial.setAction("android.intent.action.DIAL");
                dial.setData(Uri.parse("tel:" + "8888888888"));
                startActivity(dial);

            }
        });
        AlertDialog alert = builder.create();
        //https://stackoverflow.com/questions/4904603/android-alert-with-sound-vibration
        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);

        alert.show();

        // start another one

      /*  final Handler handler = new Handler();
        handler.postDelayed(new Runnable()  {
            @Override
            public void run() {
                showAlertDialog();
                // start another task
            }
        }, ALERT_INTERVAL);*/
    }

    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder(getResources().getString(R.string.direction_url));
        googleDirectionsUrl.append("origin=" + latitude + "," + longitude);
        googleDirectionsUrl.append("&destination=" + end_latitude + "," + end_longitude);
        //TODO
        googleDirectionsUrl.append("&key=" + "AIzaSyCAcfy-02UHSu2F6WeQ1rhQhkCr51eBL9g");

        return googleDirectionsUrl.toString();
    }

    //https://developers.google.com/places/web-service/search#PlaceSearchRequests
    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder(getResources().getString(R.string.places_url));
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyBj-cnmMUY21M0vnIKz0k3tD3bRdyZea-Y");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;

        Log.d("end_lat", "" + end_latitude);
        Log.d("end_lng", "" + end_longitude);
    }

    public void shareLocation(View v) {
        //open the contact list to select account
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RC_PICK_CONTACT);
        //startActivityForResult(new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI),RC_PICK_CONTACT);
    }

    //inform current loc
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //https://stackoverflow.com/questions/9496350/pick-a-number-and-name-from-contacts-list-in-android-app/29162018
        if (requestCode == RC_PICK_CONTACT && resultCode == RESULT_OK) {
            String contactName = retrieveContactName(data.getData());

            Uri uri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(numberColumnIndex);

            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameColumnIndex);

            Log.d(TAG, "number : " + number + " , name : " + name);
            sendLocationSMS(number, mLastLocation);
            messageType = LOCATION_MESSAGE;
            sendSMSMessage();
            Toast.makeText(this, "Share location with " + contactName, Toast.LENGTH_LONG).show();
            cursor.close();
        }

    }

    //https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
    //show address
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction", "Canont get Address!");
        }
        return strAdd;
    }

    public void sendLocationSMS(String phoneNumber, Location currentLocation) {
        SmsManager smsManager = SmsManager.getDefault();

        StringBuffer smsBody = new StringBuffer(); //currentLocation.getLatitude(),currentLocation.getLongitude()
        smsBody.append("I am sharing my current location with you: " + getCompleteAddressString(latitude, longitude));//"http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude()
        smsBody.append(currentLocation.getLatitude());
        smsBody.append(",");
        smsBody.append(currentLocation.getLongitude());
        //smsManager.sendTextMessage(phoneNumber, null, smsBody.toString(), null, null);

        //https://www.tutorialspoint.com/android/android_sending_sms.htm
        //https://stackoverflow.com/questions/6580675/how-to-send-the-sms-more-than-160-character
        //show in message
        String smsBody1 = new String();
        smsBody1 = "I am sharing my current location with you: " + getCompleteAddressString(latitude, longitude) + "at latitude of " + currentLocation.getLatitude() + ", and longitude of " + currentLocation.getLongitude();
        ArrayList<String> parts = smsManager.divideMessage(smsBody1);
        smsManager.sendTextMessage(phoneNumber, null, smsBody1.toString(), null, null);
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
    }

    //https://www.tutorialspoint.com/android/android_sending_sms.htm
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    protected void sendSMSMessage() {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            sendSMSAfterPermissionGranted();
        }

    }


    private String retrieveContactName(Uri uriContact) {

        String contactName = null;

        //querying contact data store
        Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);

        if (cursor.moveToFirst()) {

            //DISPLAY_NAME=Thedisplaynameforthecontact.
            //HAS_PHONE_NUMBER=Anindicatorofwhetherthiscontacthasatleastonephonenumber.

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        return contactName;

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("MapActivity", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MapActivity", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("MapActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("MapActivity", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("MapActivity", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MapActivity", "onDestroy");
    }




}