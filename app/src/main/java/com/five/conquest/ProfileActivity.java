package com.five.conquest;

import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    ImageButton mLevelUpAttack;
    ImageButton mLevelUpDefense;
    Button mSaveStats;

    TextView mLevel;
    TextView mEXP;
    TextView mAttack;
    TextView mDefense;
    TextView mPoints;
    TextView mDistance;
    TextView mUsername;

    User player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mLevelUpAttack = (ImageButton) findViewById(R.id.imageButtonAttack);
        mLevelUpDefense = (ImageButton) findViewById(R.id.imageButtonDefense);

        mSaveStats = (Button) findViewById(R.id.buttonSaveStats);

        mLevel = (TextView) findViewById(R.id.textViewLevel);
        mEXP = (TextView) findViewById(R.id.textViewExp);
        mPoints = (TextView) findViewById(R.id.textViewPoints);
        mAttack = (TextView) findViewById(R.id.textViewAttackPoints);
        mDefense = (TextView) findViewById(R.id.textViewDefensePoints);
        mDistance = (TextView) findViewById(R.id.textViewDistance);
        mUsername = (TextView) findViewById(R.id.textViewUser);
    }

    @Override
    public void onStart() {
        super.onStart();
        player = new User("Player 1");

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
        mLevel.setText("Level " + player.level);
        mEXP.setText("EXP: " + player.exp);
        mAttack.setText(player.attack);
        mDefense.setText(player.defense);
        mDistance.setText("Distance Traveled: " + player.distance.toString() + " miles");
        mUsername.setText(player.username);

        if(player.points > 0) {
            mSaveStats.setText("Points to allocate: " + player.points);
            mLevelUpAttack.setVisibility(View.VISIBLE);
            mLevelUpDefense.setVisibility(View.VISIBLE);
        } else {
            mSaveStats.setVisibility(View.INVISIBLE);
            mPoints.setVisibility(View.INVISIBLE);
            mLevelUpAttack.setVisibility(View.INVISIBLE);
            mLevelUpDefense.setVisibility(View.INVISIBLE);
        }
    }



}
