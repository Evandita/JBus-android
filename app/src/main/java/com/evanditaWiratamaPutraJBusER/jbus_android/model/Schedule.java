package com.evanditaWiratamaPutraJBusER.jbus_android.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Schedule {
    public Timestamp departureSchedule;
    public Map<String, Boolean> seatAvailability;

    public static List<Schedule> sampleScheduleList(int size) {
        List<Schedule> scheduleList = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            Schedule schedule = new Schedule();
            schedule.departureSchedule = Timestamp.valueOf("2023-7-18 15:00:00");

            scheduleList.add(schedule);
        }

        return scheduleList;
    }
}
