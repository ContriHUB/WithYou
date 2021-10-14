package com.example.withyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    EditText polnumber,textnumber;
    MaterialButton set_button;
    MaterialToolbar toolbar;
    public static final String SHARED_PREFS="sharedPrefs";
    public static final String CALL="call";
    public static final String TEXT="text";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        polnumber=findViewById(R.id.polnumber);
        textnumber=findViewById(R.id.textnumber);
        set_button=findViewById(R.id.set_button);

        loadUpData();

        set_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveData();
                Toast.makeText(getBaseContext(), R.string.details_set,Toast.LENGTH_SHORT).show();
                loadData();
            }
        });
  //BackFunction Added
        toolbar = (MaterialToolbar)findViewById(R.id.topAppBar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                Intent i= new Intent(SettingsActivity.this,AccessActivity.class);
                startActivity(i);
            }
        });

    }

    public void loadUpData(){

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        polnumber.setText(sharedPreferences.getString(CALL,""));
        textnumber.setText(sharedPreferences.getString(TEXT,""));


    }

    public void saveData(){

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putString(CALL,polnumber.getText().toString());
        editor.putString(TEXT,textnumber.getText().toString());
        editor.commit();

    }
    public void loadData() {

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String callNumber=sharedPreferences.getString(CALL,"");
        String textNumber=sharedPreferences.getString(TEXT,"");

        Toast.makeText(getBaseContext(),
                getString(R.string.call_number_set_to)+callNumber+"\n"+getString(R.string.text_number_set_to)+textNumber,Toast.LENGTH_SHORT).show();

    }

}
