package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;
import android.content.*;
import android.graphics.drawable.*;
import android.widget.*;
import android.os.*;
import android.view.animation.*;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private Handler handler;
    private int currentImageIndex = 0;
    private int[] gradientImages = { R.drawable.gradient_bg3, R.drawable.gradient_bg3_3, R.drawable.gradient_bg3_4, R.drawable.gradient_bg3_2};
    private static final int FADE_DURATION = 3000;

    private Animation slideInLeft;
    private Animation slideInRight;
    private Animation slideOutLeft;
    private Animation slideOutRight;

    private TextView title;
    private EditText username;
    private EditText password;
    private Button login;
    private TextView account;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);

        linearLayout = findViewById(R.id.login_page);
        handler = new Handler();
        animateBackground();

        title = findViewById(R.id.login_title);
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_login);
        account = findViewById(R.id.login_account);
        register = findViewById(R.id.login_register);

        animationIn();

        register.setOnClickListener(v -> {
            animationOut();

            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        });


        login.setOnClickListener(v -> {
            animationOut();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

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

    private void animationIn() {
        title.startAnimation(slideInLeft);
        username.startAnimation(slideInRight);
        password.startAnimation(slideInLeft);
        login.startAnimation(slideInRight);
        account.startAnimation(slideInLeft);
        register.startAnimation(slideInRight);
    }

    private void animationOut() {
        title.startAnimation(slideOutRight);
        username.startAnimation(slideOutLeft);
        password.startAnimation(slideOutRight);
        login.startAnimation(slideOutLeft);
        account.startAnimation(slideOutRight);
        register.startAnimation(slideOutLeft);
    }

}