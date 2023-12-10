package com.evanditaWiratamaPutraJBusER.jbus_android;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import java.util.List;

public class ManageBusArrayAdapter extends ArrayAdapter<Bus>{
    public ManageBusArrayAdapter(Context context, List<Bus> buses) {
        super(context, 0, buses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bus bus = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.manage_bus_view, parent, false);
        }

        TextView busName = convertView.findViewById(R.id.manageBusView_busName);
        TextView busPrice = convertView.findViewById(R.id.manageBusView_busPrice);
        TextView busCapacity = convertView.findViewById(R.id.manageBusView_busCapacity);
        TextView busBusType = convertView.findViewById(R.id.manageBusView_busBusType);
        TextView busDeparture = convertView.findViewById(R.id.manageBusView_busDeparture);
        TextView busArrival = convertView.findViewById(R.id.manageBusView_busArrival);
        //ImageView busSchedule = convertView.findViewById(R.id.manageBusView_addSchedule);

        busName.setText(bus.name);
        busPrice.setText(String.valueOf(bus.price.price));
        busCapacity.setText(String.valueOf(bus.capacity));
        busBusType.setText(bus.busType.name());
        busDeparture.setText(bus.departure.stationName);
        busArrival.setText(bus.arrival.stationName);

        /*
        busSchedule.setOnClickListener(v->{
            Intent i = new Intent(ManageBusActivity.ctx, BusScheduleActivity.class);
            //i.putExtra("busId", bus.id);
            ManageBusActivity.ctx.startActivity(i);
        });

         */


        return convertView;
    }
}
