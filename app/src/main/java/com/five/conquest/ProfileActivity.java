package com.five.conquest;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.games.Player;

import static android.R.style.Animation;

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

    Drawable background;

    ProgressBar mExpBar;

    android.view.animation.Animation jiggle;

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
        background = getResources().getDrawable(R.drawable.knight);

        jiggle = AnimationUtils.loadAnimation(this, R.anim.wiggle);

        player = (User) getIntent().getSerializableExtra("player");

        background.setAlpha(20);
    }

    @Override
    public void onStart() {
        super.onStart();
//        player = new User("Player 1");

        mUsername.setPaintFlags(mUsername.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        updateView();

        mLevelUpAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.attack++;
                player.points--;

                updateView();

            }
        });

        mLevelUpDefense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.defense++;
                player.points--;

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
            mLevelUpAttack.startAnimation(jiggle);
            mLevelUpDefense.setVisibility(View.VISIBLE);
            mLevelUpDefense.startAnimation(jiggle);
        } else {
            mPoints.setVisibility(View.INVISIBLE);
            mLevelUpAttack.setVisibility(View.INVISIBLE);
            mLevelUpDefense.setVisibility(View.INVISIBLE);
            mLevelUpAttack.clearAnimation();
            mLevelUpDefense.clearAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        Log.i("CONQUEST", "Back button pressed, returning intent with settings");
        User newPlayer = player;
        Intent output = new Intent();
        output.putExtra("new player", newPlayer);
        setResult(RESULT_OK, output);
        finish();
    }



}