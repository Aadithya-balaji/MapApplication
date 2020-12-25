package com.example.googlemaps.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.googlemaps.Helper.DatabaseHepler;
import com.example.googlemaps.PlaceAdapter;
import com.example.googlemaps.PlacesModel;
import com.example.googlemaps.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    GoogleMap mGoogleMap;
    Marker currLocationMarker;
    Boolean isgps = false,curret_loc = false;
    private static final String TAG = "MainActivity";
    DatabaseHepler mydb;
    String address;
    LatLng latlng;
    Double latitude=null,longitude=null;
    ImageView history;
    public static Dialog dialog;

    ArrayList<String> list;
    ArrayList<PlacesModel> placeArrayList=new ArrayList<>();
    PlaceAdapter placeAdapter;
    Context activity;
    private Marker marker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mydb = new DatabaseHepler(this);
        history = findViewById(R.id.history);
        activity=MapsActivity.this;
        list = new ArrayList<>();

        statusCheck();

        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         searchplace();

          history.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              if (placeArrayList.size()>0) {
                  placeArrayList.clear();
              }
              DialogAlert();
          }
      });


    }


    @Override
    public void onMapReady(GoogleMap gMap) {
        mGoogleMap = gMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       mGoogleMap.setMyLocationEnabled(true);

        buildGoogleApiClient();

        mGoogleApiClient.connect();

    }

    protected synchronized void buildGoogleApiClient() {
//        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }



    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            //place marker at current position
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());


            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_location_current));
            currLocationMarker = mGoogleMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onLocationChanged(Location location) {

        if (isgps=false){
            statusCheck();
        } else {


            if (curret_loc == false) {

                if (currLocationMarker != null) {
                    currLocationMarker.remove();
                }
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_location_current));
                currLocationMarker = mGoogleMap.addMarker(markerOptions);

                //zoom to current position:
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

                //If you only need one location, unregister the listener
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                curret_loc = true;
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MapsActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                    //    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

                        mGoogleMap.setMyLocationEnabled(true);



                        buildGoogleApiClient();

                        mGoogleApiClient.connect();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    //Location permission
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        isgps = true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        isgps = false;
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //Autocomplete place search
    private void searchplace(){

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAfDkN9CGZJnwWXA93dH1EUnLgDkA-ohKg");
        }
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                mGoogleMap.clear();
                mGoogleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_location_pin_search)));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),50));
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                latlng=place.getLatLng();
                Log.e("place", String.valueOf(place.getLatLng()));

                address = place.getName();
                latitude=latlng.latitude;
                longitude=latlng.longitude;
               // Log.e("placeCC", latitude+"<><>"+longitude);

                adddata();



            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

               // Log.e("status", String.valueOf(status));

            }
        });
    }

    //Inserts data in Database
    public void adddata(){
        boolean isinserted =   mydb.insertdata(address,latitude,longitude);

        if (isinserted = true){
           // Toast.makeText(MapsActivity.this,"Success",Toast.LENGTH_LONG).show();
        } else {
           // Toast.makeText(MapsActivity.this,"Try again",Toast.LENGTH_LONG).show();

        }

    }


     //Recent search history
    private void DialogAlert() {

        dialog = new Dialog(MapsActivity.this, R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_recycler);

        Log.e("ADDs", String.valueOf(list.size()));
        list.add(address);
        Log.e("ADD", String.valueOf(list.size()));
        Cursor res = mydb.getalldata();

        if (res.getCount()==0){
            // showall("error","nothing found");
            return;
        }

        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()){

            address = res.getString(1);
            Log.e("add",address);
            Double lat1 = Double.valueOf(res.getString(2));
            Log.e("lat1", String.valueOf(lat1));


            Double lng1 = Double.valueOf(res.getString(3));
            Log.e("lng1", String.valueOf(lng1));


            PlacesModel placeModel=new PlacesModel();
            placeModel.setStrAddress(address);
            placeModel.setStrLat(String.valueOf(lat1));
            placeModel  .setStrLng(String.valueOf(lng1));

           // Log.e("name",address);

            placeArrayList.add(placeModel);

            RecyclerView recyclerView = dialog.findViewById(R.id.recycler);
            placeAdapter = new PlaceAdapter(placeArrayList,activity,mGoogleMap,dialog);

            recyclerView.setAdapter(placeAdapter);
            placeAdapter.notifyDataSetChanged();

            recyclerView.setLayoutManager(new LinearLayoutManager(MapsActivity.this.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        dialog.show();

    }

    //BitmapDescriptor can be used to load icon drawable file
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}