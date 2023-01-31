package fr.ign.geosurvey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import co.anbora.labs.spatia.builder.SpatiaRoom;
import co.anbora.labs.spatia.geometry.Point;
import fr.ign.geosurvey.data.AppDatabase;
import fr.ign.geosurvey.data.Marker;
import fr.ign.geosurvey.data.MarkerDao;

public class MarkerActivity extends AppCompatActivity implements Constants {

    private LatLng currentLatLng;

    private AppDatabase db;
    private MarkerDao markerDao;
    private EditText et_name;
    private EditText et_address;
    private EditText et_comment;
    private Button bt_create, bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        // database utils
        AppDatabase db = SpatiaRoom.INSTANCE.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class, DB_NAME).allowMainThreadQueries().build();

        markerDao = db.markerDao();

        // get views instances
        et_name = this.findViewById(R.id.et_name);
        et_address = this.findViewById(R.id.et_address);
        et_comment = this.findViewById(R.id.et_comment);
        bt_create = this.findViewById(R.id.bt_create);
        bt_cancel = this.findViewById(R.id.bt_cancel);

        // subscribe to events
        bt_create.setOnClickListener(this::btCreate_onClick);
        bt_cancel.setOnClickListener(this::btCancel_onClick);

        // get data from intent
        currentLatLng = this.getIntent().getExtras().getParcelable(MapsActivity.CURRENT_LATLNG_KEY);
        Log.i("ENSG", "Passed location: " + currentLatLng);

    }

    protected void btCreate_onClick(View view) {
        Marker m = new Marker(et_name.getText().toString(), et_address.getText().toString(), et_comment.getText().toString(), new Point(currentLatLng.longitude, currentLatLng.latitude, 4326));
        markerDao.insertAll(m);

        this.startActivity(new Intent(this, MapsActivity.class));
    }

    protected void btCancel_onClick(View view) {
        this.startActivity(new Intent(this, MapsActivity.class));
    }
}