package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Payment;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RenterPaymentActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private static Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = {R.drawable.gradient_bg9, R.drawable.gradient_bg9_2,R.drawable.gradient_bg9_4, R.drawable.gradient_bg9_3};
    private static final int FADE_DURATION = 2000;

    public static BaseApiService mApiService;
    public static Context ctx;
    private static RenterPaymentArrayAdapter adapter;
    private static ListView paymentListView;
    private TextView title;
    public static List<Payment> paymentList = new ArrayList<>();
    private static Animation fadeIn;
    private static Animation fadeOut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter_payment);
        getSupportActionBar().hide();

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.renterPayment_page);
        handler = new Handler();
        animateBackground();

        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);


        mApiService = UtilsApi.getApiService();
        ctx = this;

        paymentListView = findViewById(R.id.renterPayment_listView);
        title = findViewById(R.id.renterPayment_title);

        handleGetRenterPayment();

    }

    private static void handleGetRenterPayment() {
        mApiService.getRenterPayment(LoginActivity.loggedAccount.id, BusScheduleActivity.scheduleSelected.departureSchedule).enqueue(new Callback<BaseResponse<List<Payment>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Payment>>> call, Response<BaseResponse<List<Payment>>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<List<Payment>> res = response.body();

                // if success finish this activity (back to login activity)
                if(res.success){
                    List <Payment> tmp = res.payload;
                    paymentList.clear();
                    for (Payment i : tmp) {
                        paymentList.add(i);
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new RenterPaymentArrayAdapter(ctx, paymentList);
                            paymentListView.setAdapter(adapter);
                            paymentListView.startAnimation(fadeIn);

                        }
                    }, 1000);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Payment>>> call, Throwable t) {
                Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void handleCancel(Payment p) {
        mApiService.cancel(p.id).enqueue(new Callback<BaseResponse<Payment>>() {
            @Override
            public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Payment> res = response.body();

                // if success finish this activity (back to login activity)
                if(res.success){
                    paymentListView.startAnimation(fadeOut);
                    LoginActivity.loggedAccount.balance += ManageBusActivity.busSelected.price.price * p.busSeats.size();
                    handleGetRenterPayment();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void handleAccept(Payment p) {
        mApiService.accept(p.id).enqueue(new Callback<BaseResponse<Payment>>() {
            @Override
            public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Payment> res = response.body();

                // if success finish this activity (back to login activity)
                if(res.success){
                    paymentListView.startAnimation(fadeOut);
                    handleGetRenterPayment();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                Log.e("NetworkError", "Error: " + t.getMessage(), t);
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

    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}