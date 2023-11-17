package com.evanditaWiratamaPutraJBusER.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.view.animation.*;
import android.widget.*;
import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Load the animation
        Animation spinAnimation = AnimationUtils.loadAnimation(this, R.anim.text_anim);

        // Find views
        TextView textTitle = findViewById(R.id.register_title);
        EditText editTextUsername = findViewById(R.id.register_username);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Apply animation to views


        btnRegister.setOnClickListener(v -> {
            textTitle.startAnimation(spinAnimation);
        });

    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx,cls);
        startActivity(intent);
    }
}