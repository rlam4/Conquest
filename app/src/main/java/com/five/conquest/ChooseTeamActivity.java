package com.five.conquest;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseTeamActivity extends AppCompatActivity {

    private Drawable background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        background = getResources().getDrawable(R.drawable.compass_cropped);
        background.setAlpha(90);

        getSupportActionBar().hide();
    }


    public void onClickKnight(View v){
        String msg = "Knights Selected!";
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        Intent i = new Intent(ChooseTeamActivity.this, MainActivity.class);
        startActivity(i);

        finish();
    }

    public void onClickNinjaOrPirate(View v){
        String msg = "The team you selected has too many players already!\n" +
                "Please select another team.";
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        TextView t = (TextView) toast.getView().findViewById(android.R.id.message);
        if(t != null){
            t.setGravity(Gravity.CENTER);
        }
        toast.show();
    }
}
