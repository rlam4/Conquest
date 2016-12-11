package com.five.conquest;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    RadioButton milesRadio;
    RadioButton kmRadio;

    Switch colorblind;

    boolean kilometersSetting = false;
    boolean colorblindSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Retrieve the settings object from the intent
        final Settings settings = (Settings) getIntent().getSerializableExtra("settings");

        milesRadio = (RadioButton) findViewById(R.id.radioMilesButton);
        kmRadio = (RadioButton) findViewById(R.id.radioButtonKm);
        colorblind = (Switch) findViewById(R.id.switchCbMode);

        //Set buttons to match the user's existing settings
        kilometersSetting = settings.getKilometers();
        colorblindSetting = settings.getColorblind();

        if(kilometersSetting) {
            kmRadio.setChecked(true);
            milesRadio.setChecked(false);
        } else {
            kmRadio.setChecked(false);
            milesRadio.setChecked(true);
        }
        if(colorblindSetting) {
            colorblind.setChecked(true);
        } else {
            colorblind.setChecked(false);
        }

        //Buttons to handle selecting km vs. miles
        milesRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(milesRadio.isChecked()) {
                    kmRadio.setChecked(false);
                    kilometersSetting = false;
                }
            }
        });

        kmRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kmRadio.isChecked()) {
                    milesRadio.setChecked(false);
                    kilometersSetting = true;
                }
            }
        });

        //Buttons to handle selecting colorblind mode
        colorblind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorblind.isChecked()) {
                    colorblindSetting = true;
                    Toast.makeText(SettingsActivity.this, "Colorblind Mode: ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "Colorblind Mode: OFF", Toast.LENGTH_SHORT).show();
                    colorblindSetting = false;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Log.i("CONQUEST", "Back button pressed, returning intent with settings");
        Settings newSettings = new Settings(kilometersSetting, colorblindSetting);
        Intent output = new Intent();
        output.putExtra("new settings", newSettings);
        setResult(RESULT_OK, output);
        finish();
    }


}
