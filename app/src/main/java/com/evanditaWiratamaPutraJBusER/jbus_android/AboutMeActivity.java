package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.*;

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


        currentImageIndex = 0;
        linearLayout = findViewById(R.id.about_page);
        handler = new Handler();
        animateBackground();

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