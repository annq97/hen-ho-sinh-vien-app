package com.santteam.apphenhosinhvien;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ManHinhChaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_hinh_chao);
        CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(ManHinhChaoActivity.this, DangNhapActivity.class);
                startActivity(intent);
                finish();
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}
