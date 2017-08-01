package nz.ac.auckland.nihi.trainer.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Collection;

import nz.ac.auckland.cs.odin.android.api.services.testharness.TestHarnessUtils;
import nz.ac.auckland.nihi.trainer.R;
import nz.ac.auckland.nihi.trainer.data.DatabaseHelper;
import nz.ac.auckland.nihi.trainer.data.ExerciseSummary;
import nz.ac.auckland.nihi.trainer.data.RCExerciseSummary;
import nz.ac.auckland.nihi.trainer.data.Route;
import nz.ac.auckland.nihi.trainer.data.SummaryDataChunk;
import nz.ac.auckland.nihi.trainer.util.LocationUtils;

public class WorkoutSummaryActivity extends FragmentActivity {

    private DatabaseHelper dbHelper;
    private RCExerciseSummary exerciseSummary;
    Dao<RCExerciseSummary, String> exerciseSummariesDAO;
    Route route;

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Polyline routePolyline;
    // A value indicating if we're currently showing the map.
    private boolean isShowingMap = false;

    /**
     * Lazily creates the {@link #dbHelper} if required, then returns it.
     *
     * @return
     */
    //Make a database helper
    private DatabaseHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return dbHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);
        try {
            // Get RCExerciseSummary object to be displayed
            exerciseSummariesDAO = getHelper().getExerciseSummaryDAO();
            String workoutId = getIntent().getExtras().getString("workout_id");
            exerciseSummary = exerciseSummariesDAO.queryForId(workoutId);

            route = getHelper().getRoutesDAO().queryForId(exerciseSummary.getFollowedRoute().getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.summary_map);
        // Add a custom marker to the map to show the user's location.
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                final MarkerOptions markerOpt = new MarkerOptions();
//                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.nihi_map_marker)).anchor(0.5f, 1f)
//                        .draggable(false).position(new LatLng(0, 0));
//                WorkoutActivity.this.userMapMarker = googleMap.addMarker(markerOpt);
                WorkoutSummaryActivity.this.mMap = googleMap;
                PolylineOptions polyOpt = new PolylineOptions();
                polyOpt.add(LocationUtils.toLatLngArray(route)).color(Color.RED).width(10);
                if (WorkoutSummaryActivity.this.mMap != null) {
                    routePolyline = WorkoutSummaryActivity.this.mMap.addPolyline(polyOpt);
                }

                addMarkers();
                showOnMap(new LatLng(route.getGpsCoordinates().iterator().next().getLatitude(),route.getGpsCoordinates().iterator().next().getLongitude()));
            }
        });


    }

    private void addMarkers() {
        Collection<SummaryDataChunk> summaryDataChunks = exerciseSummary.getSummaryDataChunks();
        for (SummaryDataChunk s: summaryDataChunks) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(s.getLatitude(), s.getLongitude())).title("time")
                    .snippet("Heart rate (bpm): " + s.getHeartRate() + "\nSpeed (km/h): " + s.getSpeed()));
        }

    }

    /**
     * Shows the given location on the map, if the map is visible.
     *
     * @param location
     */
    private void showOnMap(LatLng gmLocation) {
        final double RADIUS_KM = 1.5; // the distance, in KM, to show around the point.

//        if (!TestHarnessUtils.isTestHarness() && isShowingMap) {
//            LatLng gmLocation = new LatLng(location.getLatitude(), location.getLongitude());

            // For now, just display RADIUS_KM in all directions around the given location.
            LatLng northBounds = LocationUtils.getLocation(gmLocation, 0, RADIUS_KM);
            LatLng southBounds = LocationUtils.getLocation(gmLocation, 180, RADIUS_KM);
            LatLng eastBounds = LocationUtils.getLocation(gmLocation, 90, RADIUS_KM);
            LatLng westBounds = LocationUtils.getLocation(gmLocation, 270, RADIUS_KM);

            LatLngBounds bounds;
            int padding;

            if (routePolyline == null) {
                bounds = LatLngBounds.builder().include(northBounds).include(southBounds).include(eastBounds)
                        .include(westBounds).include(gmLocation).build();
                padding = 0;
            } else {
                LatLngBounds.Builder builder = LatLngBounds.builder().include(gmLocation);
                for (LatLng pt : routePolyline.getPoints()) {
                    builder.include(pt);
                }
                bounds = builder.build();
                padding = 100;
            }

            final CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            if (this.mMap != null) {
                try {
                    this.mMap.moveCamera(camUpdate);

                } catch (IllegalStateException ex) {
                    // Map not yet initialized.
//                    logger.info("showOnMap(): IllegalStateException when trying to move camera. Will delegate task to listener.");
                    final View mapView = mapFragment.getView();
                    if (mapView.getViewTreeObserver().isAlive()) {
                        mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @SuppressWarnings("deprecation")
                            @SuppressLint("NewApi")
                            @Override
                            public void onGlobalLayout() {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                    mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                } else {
                                    mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                                WorkoutSummaryActivity.this.mMap.moveCamera(camUpdate);
                            }
                        });
                    }
                }
            }
//        }
    }
}
