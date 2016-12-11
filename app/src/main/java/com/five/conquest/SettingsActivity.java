package com.five.conquest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity {

    RadioButton milesRadio;
    RadioButton kilometersRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        milesRadio = (RadioButton) findViewById(R.id.radioMilesButton);

    }


}
