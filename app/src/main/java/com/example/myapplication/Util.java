package com.example.myapplication;

import android.app.Activity;
import android.widget.Toast;

import java.util.ArrayList;

public class Util {

    public static double kmToMiles(double km) {
        return 0.6213712*km;
    }

    public static void makeToast(Activity ctx, String toast) {
        Toast.makeText(ctx, toast, Toast.LENGTH_LONG).show();
    }

    public static boolean strToBool(String s) { return s.equals("1");}

    public static ArrayList<Location> deserializeLocations(String serializedLocations) {
        ArrayList<Location> locations = new ArrayList<>();

        String[] serLocationObjs = serializedLocations.split("\\|\\|\\|\\|");
        for (String serLocObj : serLocationObjs) {
            Location l = new Location();
            String[] serLocProps = serLocObj.split("\\|\\|\\|");
            l.name = serLocProps[0];
            l.latitude = Double.parseDouble(serLocProps[1]);
            l.longitude = Double.parseDouble(serLocProps[2]);
            l.strHours = serLocProps[3];
            l.favorite = strToBool(serLocProps[4]);
            l.open = strToBool(serLocProps[5]);

            // extract location hours
            ArrayList<TimeBlock> timeBlocks = new ArrayList<>();
            String[] serTimeBlocks = serLocProps[6].split("\\|\\|");
            for (String serTimeBlock : serTimeBlocks) {
                TimeBlock tb = new TimeBlock();
                String[] serTBProps = serTimeBlock.split("\\|");
                tb.label = serTBProps[0];
                tb.start = Integer.parseInt(serTBProps[1]);
                tb.end = Integer.parseInt(serTBProps[2]);
                timeBlocks.add(tb);
            }
            l.hours = timeBlocks;

            locations.add(l);
        }

        return locations;
    }
}

