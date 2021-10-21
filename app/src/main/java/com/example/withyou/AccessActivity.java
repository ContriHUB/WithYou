package com.example.withyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

public class AccessActivity extends AppCompatActivity {
// using Class LottieAnimationView to get animation
    LottieAnimationView controls,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);
// assign id's
        controls=findViewById(R.id.controls);
        settings=findViewById(R.id.settings);

        controls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccessActivity.this,MainActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccessActivity.this,SettingsActivity.class));
            }
        });




    }
}
