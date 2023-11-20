package com.evanditaWiratamaPutraJBusER.jbus_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import java.util.List;

public class BusArrayAdapter extends ArrayAdapter<Bus> {
    public BusArrayAdapter(Context context, List<Bus> buses) {
        super(context, 0, buses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bus bus = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bus_view, parent, false);
        }

        TextView busName = convertView.findViewById(R.id.busName);

        busName.setText(bus.name);

        return convertView;
    }
}