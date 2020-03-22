package me.zkk.co.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import me.zkk.co.MainActivity;
import me.zkk.co.R;

public class Welcome extends AppCompatActivity {

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            startActivity(new Intent(Welcome.this, MainActivity.class));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //延时操作
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);
    }
}
