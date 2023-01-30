package fr.ign.geosurvey;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fr.ign.geosurvey.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    // Create attributes for future component handling
    private Button bt_point, bt_topology;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Find components instances
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
    }

    // always create event handling as this for better readiness of the code
    protected void btPoint_onClicked(View view) {
        Intent myIntent = new Intent(this, PointActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        this.startActivity(myIntent);
    }

    protected void btTopology_onClicked(View view) {
        Intent myIntent = new Intent(this, TopologyActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        this.startActivity(myIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(48.8410651, 2.587376);
        mMap.addMarker(new MarkerOptions().position(sydney).title("ENSG"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
    }
}