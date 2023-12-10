package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.*;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BusType;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Facility;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Station;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import java.io.IOException;
import java.util.*;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class AddBusActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = {R.drawable.gradient_bg9, R.drawable.gradient_bg9_2,R.drawable.gradient_bg9_4, R.drawable.gradient_bg9_3};
    private static final int FADE_DURATION = 2000;
    private BusType[] busType = BusType.values();
    private BusType selectedBusType;
    private Spinner busTypeSpinner;
    private Spinner departureSpinner;
    private Spinner arrivalSpinner;
    private List<Station> stationList = new ArrayList<>();
    private int selectedDeptStationID;
    private int selectedArrStationID;

    private BaseApiService mApiService;
    private Context ctx;

    private TextView title,facilitiesL;
    LinearLayout busTypeL, departureL, arrivalL,
             facilities_1L, facilities_2L, facilities_3L;
    private EditText busName, capacity, price;
    private CheckBox acCheckBox, wifiCheckBox, toiletCheckBox, lcdCheckBox,
            coolboxCheckBox, lunchCheckBox, baggageCheckBox, electricCheckBox;
    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;
    private List<Facility> selectedFacilities = new ArrayList<>();
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        getSupportActionBar().hide();

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.addbus_page);
        handler = new Handler();
        animateBackground();


        busName = findViewById(R.id.addbus_name);
        capacity = findViewById(R.id.addbus_capacity);
        price = findViewById(R.id.addbus_price);
        title = findViewById(R.id.addbus_title);
        busTypeL = findViewById(R.id.addbus_bus_type);
        departureL = findViewById(R.id.addbus_departure);
        arrivalL = findViewById(R.id.addbus_arrival);
        facilitiesL = findViewById(R.id.addbus_facilities);
        facilities_1L = findViewById(R.id.addbus_facilities_1);
        facilities_2L = findViewById(R.id.addbus_facilities_2);
        facilities_3L = findViewById(R.id.addbus_facilities_3);


        acCheckBox = findViewById(R.id.addbus_ac);
        wifiCheckBox = findViewById(R.id.addbus_wifi);
        toiletCheckBox = findViewById(R.id.addbus_toilet);
        lcdCheckBox = findViewById(R.id.addbus_lcd);
        coolboxCheckBox = findViewById(R.id.addbus_coolbox);
        lunchCheckBox = findViewById(R.id.addbus_lunch);
        baggageCheckBox = findViewById(R.id.addbus_baggage);
        electricCheckBox = findViewById(R.id.addbus_electric);

        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        add = findViewById(R.id.addbus_add);

        mApiService = UtilsApi.getApiService();
        ctx = this;
        selectedFacilities.clear();


        busTypeSpinner = this.findViewById(R.id.bus_type_dropdown);
        departureSpinner = this.findViewById(R.id.departure_station_dropdown);
        arrivalSpinner = this.findViewById(R.id.arrival_station_dropdown);
        ArrayAdapter adBus = new ArrayAdapter(this, R.layout.custom_spinner, busType);
        adBus.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        busTypeSpinner.setAdapter(adBus);
        // menambahkan OISL (OnItemSelectedListener) untuk spinner
        busTypeSpinner.setOnItemSelectedListener(busTypeOISL);

        handleGetAllStation();

        animationIn();

        add.setOnClickListener(v -> {

            if (acCheckBox.isChecked()) { selectedFacilities.add(Facility.AC);}
            if (wifiCheckBox.isChecked()) { selectedFacilities.add(Facility.WIFI);}
            if (toiletCheckBox.isChecked()) { selectedFacilities.add(Facility.TOILET);}
            if (lcdCheckBox.isChecked()) { selectedFacilities.add(Facility.LCD_TV);}
            if (coolboxCheckBox.isChecked()) { selectedFacilities.add(Facility.COOL_BOX);}
            if (lunchCheckBox.isChecked()) { selectedFacilities.add(Facility.LUNCH);}
            if (baggageCheckBox.isChecked()) { selectedFacilities.add(Facility.LARGE_BAGGAGE);}
            if (electricCheckBox.isChecked()) { selectedFacilities.add(Facility.ELECTRIC_SOCKET);}


            handleBusCreate();

        });

    }

    AdapterView.OnItemSelectedListener busTypeOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            // mengisi field selectedBusType sesuai dengan item yang dipilih
            selectedBusType = busType[position];
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    AdapterView.OnItemSelectedListener deptOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            // mengisi field selectedBusType sesuai dengan item yang dipilih
            selectedDeptStationID = stationList.get(position).id;
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    AdapterView.OnItemSelectedListener arrOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
            // mengisi field selectedBusType sesuai dengan item yang dipilih
            selectedArrStationID = stationList.get(position).id;
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    protected void handleGetAllStation() {
        mApiService.getAllStation().enqueue(new Callback<BaseResponse<List<Station>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Station>>> call, Response<BaseResponse<List<Station>>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Log.e("MyApp", "Application error: " + response.code());
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<List<Station>> res = response.body();

                // if success finish this activity (back to login activity)
                if(res.success){
                    stationList = res.payload; //simpan response body ke listStation
                    List<String> stationName = new ArrayList<>();
                    for (Station i : stationList) {
                        stationName.add(i.stationName);
                    }
                    ArrayAdapter deptBus = new ArrayAdapter(ctx, R.layout.custom_spinner, stationName);
                    deptBus.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                    departureSpinner.setAdapter(deptBus);
                    departureSpinner.setOnItemSelectedListener(deptOISL);

                    ArrayAdapter arrBus = new ArrayAdapter(ctx, R.layout.custom_spinner, stationName);
                    arrBus.setDropDownViewResource(R.layout.custom_spinner_dropdown);
                    arrivalSpinner.setAdapter(arrBus);
                    arrivalSpinner.setOnItemSelectedListener(arrOISL);
                }
                //Toast.makeText(ctx, res.message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Station>>> call, Throwable t) {
                //Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void handleBusCreate() {
        int idS = LoginActivity.loggedAccount.id;
        String nameS = busName.getText().toString();
        String capacityS = capacity.getText().toString();
        String priceS = price.getText().toString();

        if (nameS.isEmpty() || capacityS.isEmpty() || priceS.isEmpty()) {
            Toast.makeText(ctx, "Field cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int capacityI = Integer.parseInt(capacityS);
        int priceI = Integer.parseInt(priceS);


        mApiService.create(idS, nameS, selectedFacilities, priceI, capacityI, selectedBusType, selectedDeptStationID, selectedArrStationID).enqueue(new Callback<BaseResponse<Bus>>() {
            @Override
            public void onResponse(Call<BaseResponse<Bus>> call, Response<BaseResponse<Bus>> response) {

                if (!response.isSuccessful()) {

                    Log.e("MyApp", "Application error: " + response.code());
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("MyApp", "Error Body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    return;
                }
                BaseResponse<Bus> res = response.body();
                if(res.success){
                    Toast.makeText(ctx, "Bus berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    animationOut();
                    finish();

                }
                Toast.makeText(ctx, "Bus gagal ditambahkan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<BaseResponse<Bus>> call, Throwable t) {
                //Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
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

    public void animationOut(){

        title.startAnimation(slideOutRight);

        busName.startAnimation(slideOutLeft);
        capacity.startAnimation(slideOutRight);
        price.startAnimation(slideOutLeft);

        busTypeL.startAnimation(slideOutRight);
        departureL.startAnimation(slideOutLeft);
        arrivalL.startAnimation(slideOutRight);

        facilitiesL.startAnimation(slideOutLeft);
        facilities_1L.startAnimation(slideOutRight);
        facilities_2L.startAnimation(slideOutLeft);
        facilities_3L.startAnimation(slideOutRight);

        add.startAnimation(slideOutLeft);

    }

    public void animationIn () {
        title.startAnimation(slideInLeft);

        busName.startAnimation(slideInRight);
        capacity.startAnimation(slideInLeft);
        price.startAnimation(slideInRight);

        busTypeL.startAnimation(slideInLeft);
        departureL.startAnimation(slideInRight);
        arrivalL.startAnimation(slideInLeft);

        facilitiesL.startAnimation(slideInRight);
        facilities_1L.startAnimation(slideInLeft);
        facilities_2L.startAnimation(slideInRight);
        facilities_3L.startAnimation(slideInLeft);

        add.startAnimation(slideInRight);
    }

    @Override
    public void onBackPressed() {

        animationOut();

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}