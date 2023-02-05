package fr.ign.geosurvey;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import co.anbora.labs.spatia.builder.SpatiaRoom;
import fr.ign.geosurvey.data.AppDatabase;
import fr.ign.geosurvey.data.Marker;
import fr.ign.geosurvey.data.MarkerDao;
import fr.ign.geosurvey.data.Topology;
import fr.ign.geosurvey.data.TopologyDao;
import fr.ign.geosurvey.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements Constants, OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private ActivityMapsBinding binding;

    // Create attributes for future component handling
    private Button bt_point, bt_topology;

    private LatLng currentLatLng;
    private boolean isRecording = false;
    private ArrayList<LatLng> topo;

    private AppDatabase db;
    private MarkerDao markerDao;
    private TopologyDao topologyDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load xml view
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // load map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // database utils
        AppDatabase db = SpatiaRoom.INSTANCE.databaseBuilder(getApplicationContext(),
                AppDatabase.class, DB_NAME).allowMainThreadQueries().build();

        markerDao = db.markerDao();
        topologyDao = db.topologyDao();

        // find components instances
        bt_point = this.findViewById(R.id.bt_point);
        bt_topology = this.findViewById(R.id.bt_topology);

        // manage events
        bt_point.setOnClickListener(new View.OnClickListener() { // old school with anonymous classes
            @Override
            public void onClick(View view) {
                MapsActivity.this.btPoint_onClicked(view);
            }
        });

        bt_topology.setOnClickListener(this::btTopology_onClicked); // new flavour with java 8 lambda syntactic sugar

        // location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // grant permission
        grantPermission();

    }

    // always create event handling as this for better readiness of the code
    protected void btPoint_onClicked(View view) {
        Intent itent = new Intent(this, MarkerActivity.class);
        itent.putExtra(CURRENT_LATLNG_KEY, currentLatLng); //Optional parameters
        this.startActivity(itent);
    }

    protected void btTopology_onClicked(View view) {
        // inverse boolean (toggling)
        isRecording = !isRecording;

        if (isRecording) {
            // toggle button text
            bt_topology.setText(R.string.fin_topo);

            // initialize path to record new one
            topo = new ArrayList<>();
        } else {
            // toggle button text
            bt_topology.setText(R.string.add_topo);

            Intent intent = new Intent(this, TopologyActivity.class);
            intent.putParcelableArrayListExtra(TOPO_KEY, topo);
            this.startActivity(intent);
        }
    }

    protected void grantPermission() {
        // Demande la permission d'accès à la position
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                Log.i("ENSG", "fineLocationGranted");
                                getCurrentLocation();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Log.i("ENSG", "coarseLocationGranted");
                                getCurrentLocation();
                            } else {
                                // No location access granted.
                                Log.e("ENSG", "noLocationGranted");
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    protected void getCurrentLocation() throws SecurityException {
        Log.i("ENSG", "get currentLocation");
        // Définition des paramètres de notre requête de mise à jour de la position
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(500)
                .setMaxUpdateDelayMillis(1000)
                .build();
        // Callback de mise à jour
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("GPS", "currentLocation : " + location);

                    // save current location
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    // add to path if in record mode
                    if (isRecording) topo.add(new LatLng(location.getLatitude(), location.getLongitude()));
                }
                updateMap();
            };

        };
        // Lancement de la demande
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    protected void loadDBMarkers() {
        List<Marker> markers = markerDao.getAll();
        for (Marker marker : markers) {
            LatLng latLng = new LatLng(marker.position.getY(), marker.position.getX());

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(marker.name)
                    .snippet(marker.address + "\n" + marker.comment));
        }
    }

    protected void loadDBTopologies() {
        List<Topology> topologies = topologyDao.getAll();
        for (Topology topology : topologies) {
            mMap.addPolyline(new PolylineOptions().addAll(GeoConverters.lineString2LatLngs(topology.path)));

            // add a first marker
            LatLng firstLatLng = GeoConverters.point2LatLng(topology.path.getPoints().stream().findFirst().get());
            mMap.addMarker(new MarkerOptions()
                    .position(firstLatLng)
                    .title(topology.name)
                    .snippet(topology.comment)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_path)));
        }
    }

    private void updateMap() {
        if (currentLatLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        loadDBMarkers();
        loadDBTopologies();
        getCurrentLocation();

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }
}