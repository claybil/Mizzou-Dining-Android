package com.example.myapplication;

// WARNING: this code may induce vomiting, it was produced in a rush and is not production ready

import static com.example.myapplication.Util.makeToast;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.databinding.FragmentSearchBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.color.DynamicColors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public List<Location> allLocations;
    public List<Location> openLocations;
    public List<String> strAllLocations;
    public List<String> strOpenLocations;

    public boolean hasLoc;

    public double userLat = 0.0;
    public double userLong = 0.0;

    final static List<String> SAMPLE_FAVS = Arrays.asList("Baja Grill", "Potential Energy Café",
            "Sunshine Sushi", "Sabai");
    public List<String> sampleFavorites;

    // If debug mode is on, the cached locations.html will be used instead of live data.
    final static boolean DEBUG_MODE = false;

    // relative to app/src/main/assets/
    final static String DEBUG_HTML_FN = "locations.html";

    // Used to load the 'myapplication' library on application startup.
    static {
        System.loadLibrary("myapplication");
    }

    private ActivityMainBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
//    private FragmentSearchBinding binding2;

    @SuppressLint("MissingPermission")
    public void tryGetLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double userLat = location.getLatitude();
                        double userLong = location.getLongitude();
                        MainActivity.this.userLat = userLat;
                        MainActivity.this.userLong = userLong;
                        String locStr = userLat + ", " + userLong;
//                        makeToast(MainActivity.this, locStr);

                        // update UI with location data
                        while (MainActivity.this.findViewById(R.id.nearby_list) == null) {
                            // block
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ListView nearbyList = MainActivity.this.findViewById(R.id.nearby_list);

                        // we got new location data, refresh the "nearby" list

                        System.out.println("1");
                        MainActivity mainActivity = MainActivity.this;

                        // calculate the distance from the user to each location
                        for (Location l : mainActivity.allLocations) {
                            double kmDist = HaversineAlgorithm.HaversineInKM(mainActivity.userLat, mainActivity.userLong,
                                    l.latitude, l.longitude);
                            double miDist = Util.kmToMiles(kmDist);
                            l.distanceToUserMi = miDist;
                        }

                        // this creates a "shallow" copy
                        // the order of the new list is not reflected in the original list
                        List<Location> sortedLocations = new ArrayList<>(mainActivity.allLocations);

                        sortedLocations.sort(new Comparator<Location>() {
                            @Override
                            public int compare(Location lhs, Location rhs) {
                                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                return Double.compare(rhs.distanceToUserMi, lhs.distanceToUserMi);
                            }
                        });

                        Collections.reverse(sortedLocations);

                        System.out.println("2");

                        List<String> updatedLocStrs = new ArrayList<>();
                        for (int i = 0; i < 4; i++) { // for the first 4 closest locations
                            Location l = sortedLocations.get(i);
                            String strLocation = l.name + "\n" + l.strHours.substring(0, l.strHours.length() - 1) + "\n";
                            strLocation += String.format("%.4f", l.distanceToUserMi) + " mi away";
                            updatedLocStrs.add(strLocation);
                        }

                        System.out.println("3");

//                    mainActivity.allLocations.sort();
                        ArrayAdapter<String> a2 = new ArrayAdapter<>(mainActivity,
                                R.layout.list_item_textview,
                                updatedLocStrs);
                        nearbyList.setAdapter(a2);

                        System.out.println("4");

                    }
                    else {
                        makeToast(MainActivity.this, "got null :(");
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // BOILERPLATE: initialize stuff
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());

        // appDataDir will be something like "/data/user/0/com.example.myapplication/files"
        String appDataDir = this.getApplicationContext().getFilesDir().toString();
        String cachedHtmlPath = appDataDir + "/" + "locations.html";
        String cachedDatePath = appDataDir + "/" + "date.txt";

        // TODO: allow the user to retrieve data for future dates as well
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        // the system time is retrieved from the Android OS
        Date today = new Date();
        String todayStr = df.format(today);

        // do we need to retrieve new data, or can we use cached data?
        // (if debug mode is enabled, we always used cached data. if not, we check if we previously cached
        // downloaded data for today's date, and if so, we use that.)
        boolean useCachedData = false;
        boolean debugDataSet = false;
        // this automatically closes the file outside the scope, like Python `with`

        if (DEBUG_MODE) { useCachedData = true; }

        try (InputStream is = new FileInputStream(cachedDatePath)) {
            Scanner scanner = new Scanner(is).useDelimiter("\\A"); String contents = scanner.hasNext() ? scanner.next() : "";
            if (contents.equals(todayStr)) {
                useCachedData = true;
            }
            else if (contents.equals("DEBUG")) {
                debugDataSet = true;
            }
        } catch (IOException ignored) {}

        if (DEBUG_MODE && !debugDataSet) {
            // copy from assets/<DEBUG_HTML_FN> to cachedHtmlPath (<appDataDir>/locations.html)
            AssetManager am = this.getAssets();
            InputStream is; try { is = am.open(DEBUG_HTML_FN); } catch (IOException e) { throw new RuntimeException(e); }
            Scanner scanner = new Scanner(is).useDelimiter("\\A"); String contents = scanner.hasNext() ? scanner.next() : "";
            try { is.close(); } catch (IOException e) { throw new RuntimeException(e); }
            PrintWriter pw; try { pw = new PrintWriter(cachedHtmlPath); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
            pw.print(contents); pw.close();

            // write "DEBUG" constant to cachedDatePath so we know to use the cached data when the app relaunches
            PrintWriter pw2; try { pw2 = new PrintWriter(cachedDatePath); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
            pw2.print("DEBUG"); pw2.close();
        }

        // call C++ getScheduleData() function
        // TODO: move the call to getScheduleData() to a background thread so the app doesn't hang for a second if downloading new data
        // if useCachedData is false, this writes the downloaded data to cachedHtmlPath
        String serializedLocations = getScheduleData(todayStr, useCachedData, cachedHtmlPath);

        if (!useCachedData && !DEBUG_MODE) {
            // write todayStr to cachedDatePath so we know to use the cached data when the app relaunches
            PrintWriter pw; try { pw = new PrintWriter(cachedDatePath); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
            pw.print(todayStr); pw.close();
        }

        allLocations = Util.deserializeLocations(serializedLocations);

        // extract certain info from the location data for use in the UI
        openLocations = new ArrayList<>();
        strAllLocations = new ArrayList<>();
        strOpenLocations = new ArrayList<>();
        sampleFavorites = new ArrayList<>();
        for (Location l : allLocations) {
            String strLocation = l.name + "\n" + l.strHours.substring(0, l.strHours.length() - 1); // minus trailing newline
            strAllLocations.add(strLocation);
            if (l.open) {
                openLocations.add(l);
                strOpenLocations.add(strLocation);
            }
            if (SAMPLE_FAVS.contains(l.name)) {
                sampleFavorites.add(strLocation);
            }
        }

        // more boilerplate initialization
        binding = ActivityMainBinding.inflate(getLayoutInflater()); // this creates the page fragments (I think)
        setContentView(binding.getRoot());
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_locations, R.id.navigation_home, R.id.navigation_search)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        boolean hasLocationPerm = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!hasLocationPerm) {
            ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                    .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                            android.Manifest.permission.ACCESS_FINE_LOCATION, false);
//                            Boolean coarseLocationGranted = result.getOrDefault(
//                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,false);
//                            if (fineLocationGranted != null && fineLocationGranted) {
//                                // Precise location access granted.
//                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
//                                // Only approximate location access granted.
//                            } else {
//                                // No location access granted.
//                            }
//                            if (coarseLocationGranted == null) { coarseLocationGranted = false; }

                            if (fineLocationGranted == null) { fineLocationGranted = false; }

                            if (!fineLocationGranted) {
                                makeToast(MainActivity.this, "Location not granted :(");
                            }
                            else {
                                tryGetLocation();
                                //                                            @Override
//                                            public void onSuccess(Location location) {
//                                                // Got last known location. In some rare situations this can be null.
//                                                if (location != null) {
//                                                    // Logic to handle location object
//                                                }
//                                            }
//                                fusedLocationClient.getLastLocation()
//                                        .addOnSuccessListener((OnSuccessListener<android.location.Location>) o -> {
//
//                                        });
                            }
                        }
                );
            locationPermissionRequest.launch(new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        else {
            tryGetLocation();
        }




    }

    /**
     * A native method that is implemented by the 'myapplication' native library,
     * which is packaged with this application.
     */
    public native String getScheduleData(String date, boolean useCachedData, String cachedHtmlPath);
}
