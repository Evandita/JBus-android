package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Renter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRenterActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex = 0;
    private int[] gradientImages = {R.drawable.gradient_bg4, R.drawable.gradient_bg4_2, R.drawable.gradient_bg4_4, R.drawable.gradient_bg4_3};
    private static final int FADE_DURATION = 3000;

    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;

    private TextView title;
    private EditText name;
    private EditText address;
    private EditText number;
    private Button register;

    private BaseApiService mApiService;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_renter);
        getSupportActionBar().hide();

        linearLayout = findViewById(R.id.renter_page);
        handler = new Handler();
        animateBackground();

        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        title = findViewById(R.id.renter_title);
        name = findViewById(R.id.renter_company);
        address = findViewById(R.id.renter_address);
        number = findViewById(R.id.renter_phone_number);
        register = findViewById(R.id.renter_register);
        mApiService = UtilsApi.getApiService();
        ctx = this;

        animationIn();

        register.setOnClickListener(v -> {
            handleRegisterRenter();
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

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    public void onBackPressed() {

        animationOut();

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    void animationIn() {
        title.startAnimation(slideInLeft);
        name.startAnimation(slideInRight);
        address.startAnimation(slideInLeft);
        number.startAnimation(slideInRight);
        register.startAnimation(slideInLeft);
    }

    void animationOut() {
        title.startAnimation(slideOutRight);
        name.startAnimation(slideOutLeft);
        address.startAnimation(slideOutRight);
        number.startAnimation(slideOutLeft);
        register.startAnimation(slideOutRight);
    }

    protected void handleRegisterRenter() {
        // handling empty field
        int idS = LoginActivity.loggedAccount.id;
        String nameS = name.getText().toString();
        String addressS = address.getText().toString();
        String numberS = number.getText().toString();
        if (nameS.isEmpty() || addressS.isEmpty() || numberS.isEmpty()) {
            Toast.makeText(ctx, "Field cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mApiService.registerRenter(idS, nameS, addressS, numberS).enqueue(new Callback<BaseResponse<Renter>>() {
            @Override
            public void onResponse(Call<BaseResponse<Renter>> call, Response<BaseResponse<Renter>> response) {
                // handle the potential 4xx & 5xx error
                if (!response.isSuccessful()) {
                    Toast.makeText(ctx, "Application error " +
                            response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                BaseResponse<Renter> res = response.body();
                // if success finish this activity (back to login activity)
                Toast.makeText(ctx, res.message, Toast.LENGTH_SHORT).show();
                if (res.success){
                    LoginActivity.loggedAccount.company = res.payload;
                    finish();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Renter>> call, Throwable t) {
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}