package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Facility;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Payment;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Schedule;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusDetailActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = { R.drawable.gradient_bg6, R.drawable.gradient_bg7, R.drawable.gradient_bg7_3, R.drawable.gradient_bg6_3, R.drawable.gradient_bg7_3, R.drawable.gradient_bg7};
    private static final int FADE_DURATION = 5000;

    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    private Animation fadeIn, fadeOut;
    private Animation rotate;

    private BaseApiService mApiService;
    public static Context ctx;

    private TextView title;
    private TextView price;
    private TextView busType;
    private TextView departure;
    private TextView arrival;
    private TextView facilities;
    private Spinner schedule;
    private LinearLayout busSeats;
    private TextView total;
    private TextView balance;
    private Button book;

    private LinearLayout priceL;
    private LinearLayout busTypeL;
    private LinearLayout departureL;
    private LinearLayout arrivalL;
    private LinearLayout facilitiesL;
    private LinearLayout scheduleL;
    private List<String> scheduleList = new ArrayList<>();
    private List<String> seatList = new ArrayList<>();
    private LinearLayout busSeatsL;
    private LinearLayout paymentL;

    private int currpos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_detail);
        getSupportActionBar().hide();

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.busDetail_page);
        handler = new Handler();
        animateBackground();

        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);

        mApiService = UtilsApi.getApiService();
        ctx = this;

        priceL = findViewById(R.id.busDetail_price_layout);
        busTypeL = findViewById(R.id.busDetail_busType_layout);
        departureL = findViewById(R.id.busDetail_departure_layout);
        arrivalL = findViewById(R.id.busDetail_arrival_layout);
        facilitiesL = findViewById(R.id.busDetail_facilities_layout);
        scheduleL = findViewById(R.id.busDetail_schedule_layout);
        busSeatsL = findViewById(R.id.busDetail_seats_layout);
        paymentL = findViewById(R.id.busDetail_payment_layout);

        title = findViewById(R.id.busDetail_title);
        price = findViewById(R.id.busDetail_price);
        busType = findViewById(R.id.busDetail_busType);
        departure = findViewById(R.id.busDetail_departure);
        arrival = findViewById(R.id.busDetail_arrival);
        facilities = findViewById(R.id.busDetail_facilities);
        schedule = findViewById(R.id.busDetail_schedule);
        busSeats = findViewById(R.id.busDetail_seats);
        total = findViewById(R.id.busDetail_total);
        balance = findViewById(R.id.busDetail_balance);
        book = findViewById(R.id.busDetail_book);

        title.setText(MainActivity.busSelected.name);
        price.setText("IDR " + String.valueOf(MainActivity.busSelected.price.price));
        busType.setText(MainActivity.busSelected.busType.name());
        departure.setText(MainActivity.busSelected.departure.stationName);
        arrival.setText(MainActivity.busSelected.arrival.stationName);
        total.setText("IDR 0.0");
        balance.setText("IDR " + LoginActivity.loggedAccount.balance);

        String tmp = "";
        int idx = MainActivity.busSelected.facilities.size();

        for (Facility f : MainActivity.busSelected.facilities) {
            tmp = tmp + f.name();
            if (!f.equals(MainActivity.busSelected.facilities.get(idx - 1))) {
                tmp = tmp + ", ";
            }
        }

        facilities.setText(tmp);

        for (Schedule s : MainActivity.busSelected.schedules) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(s.departureSchedule);
            scheduleList.add(formattedDate);


        }

        ArrayAdapter scheduleBus = new ArrayAdapter(ctx, R.layout.custom_spinner2, scheduleList);
        scheduleBus.setDropDownViewResource(R.layout.custom_spinner_dropdown2);
        schedule.setAdapter(scheduleBus);
        schedule.setOnItemSelectedListener(scheduleOISL);
        if (MainActivity.busSelected.schedules.size() > 0) {
            showSeats(MainActivity.busSelected.schedules.get(0));
        }

        animationIn();
        book.setOnClickListener(v -> {
            handleMakeBooking();
        });
    }

    private void animateBackground() {
        Drawable currentDrawable = getResources().getDrawable(gradientImages[currentImageIndex]);
        Drawable nextDrawable = getResources().getDrawable(gradientImages[(currentImageIndex + 1) % gradientImages.length]);

        TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{currentDrawable, nextDrawable});
        //transitionDrawable.setCrossFadeEnabled(true);
        transitionDrawable.startTransition(FADE_DURATION);

        linearLayout.setBackground(transitionDrawable);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentImageIndex = (currentImageIndex + 1) % gradientImages.length;
                animateBackground();
            }
        }, FADE_DURATION);
    }

    private void animationIn() {
        title.startAnimation(fadeIn);
        priceL.startAnimation(slideInLeft);
        busTypeL.startAnimation(slideInRight);
        departureL.startAnimation(slideInLeft);
        arrivalL.startAnimation(slideInRight);
        facilitiesL.startAnimation(slideInLeft);
        scheduleL.startAnimation(slideInRight);
        busSeatsL.startAnimation(slideInLeft);
    }

    private void animationOut() {
        title.startAnimation(fadeOut);
        priceL.startAnimation(slideOutRight);
        busTypeL.startAnimation(slideOutLeft);
        departureL.startAnimation(slideOutRight);
        arrivalL.startAnimation(slideOutLeft);
        facilitiesL.startAnimation(slideOutRight);
        scheduleL.startAnimation(slideOutLeft);
        busSeatsL.startAnimation(slideOutRight);
    }
    @Override
    public void onBackPressed() {

        animationOut();

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    AdapterView.OnItemSelectedListener scheduleOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            // mengisi field selectedBusType sesuai dengan item yang dipilih
            if (position > currpos) {
                currpos = position;
                busSeatsL.startAnimation(slideOutLeft);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        busSeatsL.startAnimation(slideInRight);
                        showSeats(MainActivity.busSelected.schedules.get(position));
                    }
                }, 1000);
            }
            else if (position < currpos){
                currpos = position;
                busSeatsL.startAnimation(slideOutRight);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        busSeatsL.startAnimation(slideInLeft);
                        showSeats(MainActivity.busSelected.schedules.get(position));
                    }
                }, 1000);
            }





        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    protected void showSeats(Schedule s) {
        seatList.clear();
        busSeats.removeAllViews();
        int cnt = 0;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen.width_320dp),
                (int) getResources().getDimension(R.dimen.height_80dp));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(205,
                170);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        for (Map.Entry<String, Boolean> entry : s.seatAvailability.entrySet()) {
            Button btns=new Button(this);
            if (entry.getValue()) {
                btns.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background8));
                btns.setOnClickListener(v -> {
                    if (seatList.contains(entry.getKey())){
                        btns.startAnimation(fadeIn);
                        btns.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background8));
                        seatList.remove(entry.getKey());
                    }
                    else {
                        btns.startAnimation(fadeIn);
                        btns.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background7));
                        seatList.add(entry.getKey());
                    }
                    total.setText("IDR " + seatList.size() * MainActivity.busSelected.price.price);
                });
            }
            else {
                btns.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_background6));
            }

            btns.setText(entry.getKey());
            btns.setTextColor(getResources().getColor(android.R.color.white));
            btns.setTypeface(ResourcesCompat.getFont(this, R.font.hammersmith_one));
            ll.addView(btns,bp);
            cnt++;
            if (cnt == 4) {
                cnt = 0;
                busSeats.addView(ll, lp);
                ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);

            }
            Boolean value = entry.getValue();
        }
        busSeats.addView(ll, lp);
        total.setText("IDR 0.0");
    }

    protected void handleMakeBooking() {

        if (seatList.size() * MainActivity.busSelected.price.price > LoginActivity.loggedAccount.balance) {
            Toast.makeText(ctx, "Balance is not enough",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        if (seatList.size() < 1) {
            Toast.makeText(ctx, "No Seat is chosen",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mApiService.makeBooking(LoginActivity.loggedAccount.id, MainActivity.busSelected.accountId, MainActivity.busSelected.id, seatList, scheduleList.get(currpos)).enqueue(new Callback<BaseResponse<Payment>>() {
            @Override
            public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Payment> res = response.body();
                Payment p = res.payload;
                // if success finish this activity (back to login activity)

                if (res.success){
                    Toast.makeText(ctx, res.message, Toast.LENGTH_SHORT).show();
                    LoginActivity.loggedAccount.balance -= seatList.size() * MainActivity.busSelected.price.price;
                    animationOut();
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {

                Log.e("API_CALL_ERROR", "Problem with the server", t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();


            }
        });
    }

}