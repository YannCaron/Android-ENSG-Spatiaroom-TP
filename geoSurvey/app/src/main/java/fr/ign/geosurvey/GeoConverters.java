package fr.ign.geosurvey;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.anbora.labs.spatia.geometry.LineString;
import co.anbora.labs.spatia.geometry.Point;

public class GeoConverters {

    public static final int SRID = 4326;

    public static Point latLng2Point(LatLng latLng) {
        return new Point(latLng.longitude, latLng.latitude, SRID);
    }

    public static LatLng point2LatLng(Point point) {
        return new LatLng(point.getY(), point.getX());
    }

    public static LineString latLngs2LineString(List<LatLng> latLngs) {
        return new LineString(latLngs.stream().map(GeoConverters::latLng2Point).collect(Collectors.toList()));
    }

    public static ArrayList<LatLng> lineString2LatLng(LineString lineString) {
        return (ArrayList<LatLng>) lineString.getPoints().stream().map(GeoConverters::point2LatLng).collect(Collectors.toList());
    }

}
