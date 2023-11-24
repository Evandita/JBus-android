package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.TestLooperManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.*;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.BaseApiService;
import com.evanditaWiratamaPutraJBusER.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex = 0;
    private int[] gradientImages = {R.drawable.gradient_bg2, R.drawable.gradient_bg2_3, R.drawable.gradient_bg2_4, R.drawable.gradient_bg2_2};
    private static final int FADE_DURATION = 3000;

    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;

    private TextView title;
    private EditText username;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView account;
    private Button login;
    private BaseApiService mApiService;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        linearLayout = findViewById(R.id.register_page); // Replace with the ID of your LinearLayout
        handler = new Handler();
        animateBackground();

        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        title = findViewById(R.id.register_title);
        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        register = findViewById(R.id.register_register);
        account = findViewById(R.id.register_account);
        login = findViewById(R.id.register_login);
        mApiService = UtilsApi.getApiService();
        ctx = this;

        animationIn();

        login.setOnClickListener(v -> {
            animationOut();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        register.setOnClickListener(v -> {
            handleRegister();

        });
    }

    public void onBackPressed() {

        animationOut();

        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        animationIn();
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

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    private void animationIn() {
        title.startAnimation(slideInLeft);
        username.startAnimation(slideInRight);
        email.startAnimation(slideInLeft);
        password.startAnimation(slideInRight);
        register.startAnimation(slideInLeft);
        account.startAnimation(slideInRight);
        login.startAnimation(slideInLeft);
    }

    private void animationOut() {
        title.startAnimation(slideOutRight);
        username.startAnimation(slideOutLeft);
        email.startAnimation(slideOutRight);
        password.startAnimation(slideOutLeft);
        register.startAnimation(slideOutRight);
        account.startAnimation(slideOutLeft);
        login.startAnimation(slideOutRight);
    }

    protected void handleRegister() {
    // handling empty field
        String nameS = username.getText().toString();
        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();
        if (nameS.isEmpty() || emailS.isEmpty() || passwordS.isEmpty()) {
            Toast.makeText(ctx, "Field cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mApiService.register(nameS, emailS, passwordS).enqueue(new Callback<BaseResponse<Account>>() {
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
                Toast.makeText(ctx, res.message, Toast.LENGTH_SHORT).show();
                if (res.success){
                    finish();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
                Toast.makeText(ctx, "Problem with the server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
