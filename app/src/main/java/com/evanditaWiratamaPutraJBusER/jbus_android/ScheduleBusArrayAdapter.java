package com.evanditaWiratamaPutraJBusER.jbus_android;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Schedule;

import java.text.SimpleDateFormat;
import java.util.List;

public class ScheduleBusArrayAdapter extends ArrayAdapter<Schedule> {
    public ScheduleBusArrayAdapter(Context context, List<Schedule> schedules) {
        super(context, R.layout.schedule_bus_view, schedules);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Schedule schedule = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.schedule_bus_view, parent, false);
        }

        TextView busSchedule = convertView.findViewById(R.id.scheduleBusView_schedule);

        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Format the Date object to a string
        String formattedDate = dateFormat.format(schedule.departureSchedule);

        busSchedule.setText(formattedDate);

        return convertView;
    }
}
