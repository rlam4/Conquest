package com.five.conquest;

import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    ImageButton mLevelUpAttack;
    ImageButton mLevelUpDefense;

    TextView mLevel;
    TextView mEXP;
    TextView mAttack;
    TextView mDefense;
    TextView mPoints;
    TextView mDistance;
    TextView mUsername;

    ProgressBar mExpBar;

    User player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mLevelUpAttack = (ImageButton) findViewById(R.id.imageButtonAttack);
        mLevelUpDefense = (ImageButton) findViewById(R.id.imageButtonDefense);

        mLevel = (TextView) findViewById(R.id.textViewLevel);
        mEXP = (TextView) findViewById(R.id.textViewExp);
        mPoints = (TextView) findViewById(R.id.textViewPoints);
        mAttack = (TextView) findViewById(R.id.textViewAttackPoints);
        mDefense = (TextView) findViewById(R.id.textViewDefensePoints);
        mDistance = (TextView) findViewById(R.id.textViewDistance);
        mUsername = (TextView) findViewById(R.id.textViewUser);

        mExpBar = (ProgressBar) findViewById(R.id.progressBarExp);
    }

    @Override
    public void onStart() {
        super.onStart();
        player = new User("Player 1");
        player.exp = 50;

        updateView();

        mLevelUpAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.attack++;
                player.points--;

                //TODO: Add a dialog asking if the user wants to add the point
                updateView();

            }
        });

        mLevelUpDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.defense++;
                player.points--;

                //TODO: Add a dialog asking if the user wants to add the point
                updateView();

            }
        });

    }

    public void updateView() {

        //Set all text fields
        mExpBar.setProgress(player.exp);

        mLevel.setText("Level " + player.level);
        mEXP.setText("EXP: " + player.exp + "/100");
        mAttack.setText(player.attack.toString());
        mDefense.setText(player.defense.toString());
        mDistance.setText("Distance Traveled: " + player.distance.toString() + " miles");
        mUsername.setText(player.username);
        mExpBar.setProgress(player.exp);

        if(player.points > 0) {
            mPoints.setText("Points to allocate: " + player.points);
            mLevelUpAttack.setVisibility(View.VISIBLE);
            mLevelUpDefense.setVisibility(View.VISIBLE);
        } else {
            mPoints.setVisibility(View.INVISIBLE);
            mLevelUpAttack.setVisibility(View.INVISIBLE);
            mLevelUpDefense.setVisibility(View.INVISIBLE);
        }
    }



}