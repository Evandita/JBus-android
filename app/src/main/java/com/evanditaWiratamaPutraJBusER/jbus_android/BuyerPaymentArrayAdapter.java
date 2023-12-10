package com.evanditaWiratamaPutraJBusER.jbus_android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.*;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class BuyerPaymentArrayAdapter extends ArrayAdapter<Payment> {
    public BuyerPaymentArrayAdapter(Context context, List<Payment> payments) {
        super(context, 0, payments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Payment p = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.buyer_payment_view, parent, false);
        }

        TextView id = convertView.findViewById(R.id.buyerPaymentView_id);
        TextView busName = convertView.findViewById(R.id.buyerPaymentView_bus);
        TextView departureDate = convertView.findViewById(R.id.buyerPaymentView_departureDate);
        TextView busSeats = convertView.findViewById(R.id.buyerPaymentView_busSeats);
        TextView status = convertView.findViewById(R.id.buyerPaymentView_status);
        TextView time = convertView.findViewById(R.id.buyerPaymentView_time);

        id.setText("Payment#" + p.id);
        String name = "";
        for (Bus i : MainActivity.listBus){
            if (i.id == p.getBusId()) {
                name = i.name;
                break;
            }
        }
        busName.setText(name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat SDFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");

        departureDate.setText(SDFormat.format(p.departureDate));

        String seats = "";
        int idx = p.busSeats.size();
        for (String i : p.busSeats) {
            seats = seats + i;
            if (!i.equals(p.busSeats.get(idx - 1))) {
                seats = seats + ", ";
            }
        }
        busSeats.setText(seats);

        status.setText(p.status.name());


        time.setText(dateFormat.format(p.time));

        return convertView;
    }
}
