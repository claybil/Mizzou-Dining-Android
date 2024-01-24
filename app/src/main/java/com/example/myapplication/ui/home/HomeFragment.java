package com.example.myapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.HaversineAlgorithm;
import com.example.myapplication.Location;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.Util;
import com.example.myapplication.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    MainActivity mainActivity;
    ListView favList;
    ListView nearbyList;
//    Timer checkLocTimer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mainActivity = (MainActivity) requireActivity();
        favList = binding.favoritesList;
        nearbyList = binding.nearbyList;

        ArrayAdapter<String> a1 = new ArrayAdapter<>(mainActivity,
                R.layout.list_item_textview,
                mainActivity.sampleFavorites);
        favList.setAdapter(a1);

//        mainActivity.tryGetLocation();

//        checkLocTimer = new Timer();
//        checkLocTimer.schedule(new TimerTask(){
//            @Override
//            public void run() {
//                if (mainActivity.userLat != 0.0) {
//                    // we got new location data, refresh the "nearby" list
//
//                    System.out.println("1");
//
//                    // calculate the distance from the user to each location
//                    for (Location l : mainActivity.allLocations) {
//                        double kmDist = HaversineAlgorithm.HaversineInKM(mainActivity.userLat, mainActivity.userLong,
//                                l.latitude, l.longitude);
//                        double miDist = Util.kmToMiles(kmDist);
//                        l.distanceToUserMi = miDist;
//                    }
//
//                    // this creates a "shallow" copy
//                    // the order of the new list is not reflected in the original list
//                    List<Location> sortedLocations = new ArrayList<>(mainActivity.allLocations);
//
//                    sortedLocations.sort(new Comparator<Location>() {
//                        @Override
//                        public int compare(Location lhs, Location rhs) {
//                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
//                            return Double.compare(rhs.distanceToUserMi, lhs.distanceToUserMi);
//                        }
//                    });
//
//                    System.out.println("2");
//
//                    List<String> updatedLocStrs = new ArrayList<>();
//                    for (int i = 0; i < 4; i++) { // for the first 4 closest locations
//                        Location l = sortedLocations.get(i);
//                        String strLocation = l.name + "\n" + l.strHours.substring(0, l.strHours.length() - 1);
//                        strLocation += l.distanceToUserMi + "\n";
//                        updatedLocStrs.add(strLocation);
//                    }
//
//                    System.out.println("3");
//
////                    mainActivity.allLocations.sort();
//                    ArrayAdapter<String> a2 = new ArrayAdapter<>(mainActivity,
//                            R.layout.list_item_textview,
//                            updatedLocStrs);
//                    nearbyList.setAdapter(a2);
//
//                    System.out.println("4");
//                }
//            }
//        }, 0, 5000);



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        checkLocTimer.cancel();
        binding = null;
    }
}