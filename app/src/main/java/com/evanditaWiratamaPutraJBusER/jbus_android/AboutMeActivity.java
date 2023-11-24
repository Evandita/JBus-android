package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.*;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutMeActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex;
    private int[] gradientImages = {R.drawable.gradient_bg7, R.drawable.gradient_bg7_3};

    private static final int FADE_DURATION = 5000;

    private Animation rotate;
    private Animation slideInRight;
    private Animation slideOutRight;

    private TextView initial;
    private TextView username;
    private TextView email;
    private TextView balance;
    private EditText amount;
    private Button topUp;
    private BaseApiService mApiService;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);


        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        initial = findViewById(R.id.about_initial);
        username = findViewById(R.id.about_username);
        email = findViewById(R.id.about_email);
        balance = findViewById(R.id.about_balance);

        initial.startAnimation(rotate);
        username.startAnimation(slideInRight);
        email.startAnimation(slideInRight);
        balance.startAnimation(slideInRight);

        username.setText(LoginActivity.loggedAccount.name.toString());
        email.setText(LoginActivity.loggedAccount.email.toString());
        balance.setText("IDR " + LoginActivity.loggedAccount.balance);
        initial.setText(Character.toString(LoginActivity.loggedAccount.name.toString().charAt(0)));

        topUp = findViewById(R.id.about_topUp);
        amount = findViewById(R.id.about_topUpAmount);
        mApiService = UtilsApi.getApiService();
        ctx = this;

        currentImageIndex = 0;
        linearLayout = findViewById(R.id.about_page);
        handler = new Handler();
        animateBackground();

        topUp.setOnClickListener(v -> {
            handleTopUp();
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

    protected void handleTopUp() {
        // handling empty field
        int idS = LoginActivity.loggedAccount.id;
        String amountS = amount.getText().toString();

        if (amountS.isEmpty()) {
            Toast.makeText(ctx, "Field cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Double amountD = Double.parseDouble(amountS);


        mApiService.topUp(idS, amountD).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Account> res = response.body();
                // if success finish this activity (back to login activity)
                if(res.success){
                    LoginActivity.loggedAccount.balance = res.payload.balance;
                }
                balance.setText("IDR " + LoginActivity.loggedAccount.balance);
                Toast.makeText(ctx, res.message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
                //Log.e("NetworkError", "Error: " + t.getMessage(), t);
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {
        initial.startAnimation(rotate);
        username.startAnimation(slideOutRight);
        email.startAnimation(slideOutRight);
        balance.startAnimation(slideOutRight);
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}