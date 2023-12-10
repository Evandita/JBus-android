package com.evanditaWiratamaPutraJBusER.jbus_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Payment;

import java.text.SimpleDateFormat;
import java.util.List;

public class RenterPaymentArrayAdapter extends ArrayAdapter<Payment> {
    public RenterPaymentArrayAdapter(Context context, List<Payment> payments) {
        super(context, 0, payments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Payment p = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.renter_payment_view, parent, false);
        }

        TextView id = convertView.findViewById(R.id.renterPaymentView_id);
        TextView buyerId = convertView.findViewById(R.id.renterPaymentView_buyer_id);
        TextView busSeats = convertView.findViewById(R.id.renterPaymentView_busSeats);
        TextView price = convertView.findViewById(R.id.renterPaymentView_price);
        ImageView accept = convertView.findViewById(R.id.renterPaymentview_accept);
        ImageView cancel = convertView.findViewById(R.id.renterPaymentview_cancel);

        id.setText("Payment#" + p.id);
        buyerId.setText(Integer.toString(p.buyerId));

        String seats = "";
        int idx = p.busSeats.size();
        for (String i : p.busSeats) {
            seats = seats + i;
            if (!i.equals(p.busSeats.get(idx - 1))) {
                seats = seats + ", ";
            }
        }
        busSeats.setText(seats);
        price.setText(Double.toString(ManageBusActivity.busSelected.price.price * p.busSeats.size()));

        cancel.setOnClickListener(v->{
            RenterPaymentActivity.handleCancel(p);
        });

        accept.setOnClickListener(v->{
            RenterPaymentActivity.handleAccept(p);
        });
        return convertView;
    }
}
