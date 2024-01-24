package com.example.myapplication;

import java.util.ArrayList;

// direct mapping to C++ class, with exception of "distanceToUser" prop
public class Location {
    public String name;
    public double latitude;
    public double longitude;
    public String strHours;
    public boolean favorite;
    public boolean open;
    public ArrayList<TimeBlock> hours;

    public double distanceToUserMi;
}
