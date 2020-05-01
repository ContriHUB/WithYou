package com.example.withyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText passkey;
    MaterialButton verify;
    String pass="";
    //comment test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        passkey=findViewById(R.id.passkey);
        verify=findViewById(R.id.verify);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass= String.valueOf(passkey.getText());
                Toast.makeText(getBaseContext(),pass,Toast.LENGTH_SHORT).show();
                if(pass.equalsIgnoreCase("qwe123")) {
                    Toast.makeText(getBaseContext(), "Welcome", Toast.LENGTH_SHORT).show();
                    startActivity(new android.content.Intent(LoginActivity.this, MainActivity.class));
                }
                else
                    Toast.makeText(getBaseContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
            }
        });



    }
}
