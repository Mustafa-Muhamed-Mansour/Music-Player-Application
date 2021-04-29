package com.Music.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{

    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTimer();

    }

    private void initTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(getApplicationContext(), ReadSongActivity.class));
                finish();
            }
        };

        new Timer().schedule(timerTask, 5500);
    }

}