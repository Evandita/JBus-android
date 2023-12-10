package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.*;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Schedule;
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
    private LinearLayout box;
    private TextView boxText;
    private Button boxButton;
    private Intent intentR;

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

        inAnimation();

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

        box = findViewById(R.id.about_box);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,  150);

        boxText = new TextView(this);
        boxText.setGravity(Gravity.CENTER);
        boxText.setTextColor(getResources().getColor(R.color.white));

        boxButton = new Button(this);
        boxButton.setGravity(Gravity.CENTER);
        boxButton.setBackgroundColor(getResources().getColor(android.R.color.black));
        boxButton.setTextColor(getResources().getColor(android.R.color.white));
        boxButton.setTypeface(ResourcesCompat.getFont(this, R.font.hammersmith_one));
        boxButton.setBackgroundResource(R.drawable.rounded_background2);

        box.addView(boxText, lp);
        box.addView(boxButton, lp);

        if (LoginActivity.loggedAccount.company == null) {
            intentR = new Intent(this, RegisterRenterActivity.class);
            boxText.setText("You're not registered as a RENTER!");
            boxButton.setText("Register Company");
        }
        else {
            intentR = new Intent(this, ManageBusActivity.class);
            boxText.setText("You're already registered as a RENTER!");
            boxButton.setText("Manage Bus");
        }

        boxButton.setOnClickListener(v -> {
            outAnimation();
            startActivity(intentR);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        outAnimation();
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inAnimation();
        balance.setText("IDR " + LoginActivity.loggedAccount.balance);
        if (LoginActivity.loggedAccount.company == null) {
            intentR = new Intent(this, RegisterRenterActivity.class);
            boxText.setText("You're not registered as a RENTER!");
            boxButton.setText("Register Company");
        }
        else {
            intentR = new Intent(this, ManageBusActivity.class);
            boxText.setText("You're already registered as a RENTER!");
            boxButton.setText("Manage Bus");
        }
    }

    public void inAnimation() {
        initial.startAnimation(rotate);
        username.startAnimation(slideInRight);
        email.startAnimation(slideInRight);
        balance.startAnimation(slideInRight);
    }

    public void outAnimation() {
        initial.startAnimation(rotate);
        username.startAnimation(slideOutRight);
        email.startAnimation(slideOutRight);
        balance.startAnimation(slideOutRight);
    }


}