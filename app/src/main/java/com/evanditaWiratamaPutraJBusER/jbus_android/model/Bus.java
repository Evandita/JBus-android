package com.evanditaWiratamaPutraJBusER.jbus_android.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Bus extends Serializable{
    public int accountId;
    public String name;
    public List<Facility> facilities;
    public Price price;
    public int capacity;
    public BusType busType;
    public Station departure;
    public Station arrival;
    public List<Schedule> schedules;

    public static List<Bus> sampleBusList(int size) {
        List<Bus> busList = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            Bus bus = new Bus();
            bus.name = "Bus " + i;
            bus.price = new Price();
            bus.price.price = 10000 + i;
            bus.capacity = 10 + i;
            bus.busType = BusType.REGULER;
            bus.departure = new Station();
            bus.departure.stationName = "Terminal Jatijajar";
            bus.arrival = new Station();
            bus.arrival.stationName = "Terminal Manggarai";
            busList.add(bus);
        }

        return busList;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
