package com.five.conquest;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    RadioButton milesRadio;
    RadioButton kmRadio;

    Switch colorblind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        milesRadio = (RadioButton) findViewById(R.id.radioMilesButton);
        kmRadio = (RadioButton) findViewById(R.id.radioButtonKm);
        colorblind = (Switch) findViewById(R.id.switchCbMode);

        //Buttons to handle selecting km vs. miles
        milesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(milesRadio.isChecked()) {
                    kmRadio.setChecked(false);
                    //TODO: write code to state that miles is activated
                }
            }
        });

        kmRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kmRadio.isChecked()) {
                    milesRadio.setChecked(false);
                    //TODO: write code to state km mode is activated
                }
            }
        });

        //Buttons to handle selecting colorblind mode
        colorblind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorblind.isChecked()) {
                    //TODO: Write code to turn colorblind mode on
                } else {
                   //TODO: WRite code to turn colorblind mode off
                }
            }
        });

    }


}
