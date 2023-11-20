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

public class RegisterActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex = 0;
    private int[] gradientImages = { R.drawable.gradient_bg2, R.drawable.gradient_bg2_3, R.drawable.gradient_bg2_4, R.drawable.gradient_bg2_2};
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

        animationIn();

        login.setOnClickListener(v -> {
            animationOut();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        register.setOnClickListener(v -> {
            viewToast(this, "Account Registered");
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
        Intent intent = new Intent(ctx,cls);
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
}