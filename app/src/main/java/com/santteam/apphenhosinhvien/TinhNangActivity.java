package com.santteam.apphenhosinhvien;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.santteam.apphenhosinhvien.R;

/**
 * Created by sy on 23/11/2017.
 */
public class TinhNangActivity extends AppCompatActivity {
    private Toolbar mToolbarGioithieu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinh_);
        mToolbarGioithieu = (Toolbar) findViewById(R.id.toolbarGioithieu);

        setSupportActionBar(mToolbarGioithieu);
        getSupportActionBar().setTitle("Giới thiệu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
