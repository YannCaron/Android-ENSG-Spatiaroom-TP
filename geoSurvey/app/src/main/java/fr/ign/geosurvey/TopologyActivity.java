package fr.ign.geosurvey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.anbora.labs.spatia.builder.SpatiaRoom;
import co.anbora.labs.spatia.geometry.LineString;
import co.anbora.labs.spatia.geometry.Point;
import co.anbora.labs.spatia.geometry.Polygon;
import fr.ign.geosurvey.data.AppDatabase;
import fr.ign.geosurvey.data.Marker;
import fr.ign.geosurvey.data.MarkerDao;
import fr.ign.geosurvey.data.Topology;
import fr.ign.geosurvey.data.TopologyDao;

public class TopologyActivity extends AppCompatActivity implements Constants {

    private List<LatLng> topo;

    private AppDatabase db;
    private TopologyDao topologyDao;
    private EditText et_name;
    private EditText et_comment;
    private Button bt_create, bt_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topology);

        // database utils
        AppDatabase db = SpatiaRoom.INSTANCE.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class, DB_NAME).allowMainThreadQueries().build();

        topologyDao = db.topologyDao();

        // get views instances
        et_name = this.findViewById(R.id.et_name);
        et_comment = this.findViewById(R.id.et_comment);
        bt_create = this.findViewById(R.id.bt_create);
        bt_cancel = this.findViewById(R.id.bt_cancel);

        // subscribe to events
        bt_create.setOnClickListener(this::btCreate_onClick);
        bt_cancel.setOnClickListener(this::btCancel_onClick);

        // get data from intent
        topo = this.getIntent().getExtras().getParcelableArrayList(MapsActivity.TOPO_KEY);
        Log.i("ENSG", "Passed topology: " + Arrays.toString(topo.toArray()));
    }

    protected void btCreate_onClick(View view) {

        Topology t = new Topology(
                et_name.getText().toString(),
                et_comment.getText().toString(),
                GeoConverters.latLngs2LineString(topo));
        topologyDao.insertAll(t);

        this.startActivity(new Intent(this, MapsActivity.class));
    }

    protected void btCancel_onClick(View view) {
        this.startActivity(new Intent(this, MapsActivity.class));
    }
}