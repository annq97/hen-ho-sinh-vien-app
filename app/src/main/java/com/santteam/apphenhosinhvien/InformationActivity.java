package com.santteam.apphenhosinhvien;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sy on 23/11/2017.
 */

public class InformationActivity extends AppCompatActivity {
    private Toolbar mToolbarInfor;
    private ListView mListView;
     ArrayList mArray;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_);
        addControls();
        addEvent();
        setSupportActionBar(mToolbarInfor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("Thông Tin");
        mArray = new ArrayList();
        mArray.add("Giới thiệu");
        mArray.add("Kiểm tra phiên bản mới");
        mArray.add("Vote 5* cho Hẹn hò sinh viên");
        mArray.add("Facebook: facebook.com/santteam");
        mArray.add("Email: Santteam2017@gmail.com");
        ArrayAdapter mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mArray);
        mListView.setAdapter(mAdapter);


    }

    private void addEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             switch (i) {
                 case 0:
                     Intent intentTN = new Intent(InformationActivity.this, TinhNangActivity.class);
                     startActivity(intentTN);
                     break;
                 case 1:
                     Toast.makeText(InformationActivity.this, "Hiện tại chưa có phiên bản mới!!", Toast.LENGTH_SHORT).show();
                     break;
                 case 2:
                     Toast.makeText(InformationActivity.this, "Hiện tại chưa có phiên bản mới!!", Toast.LENGTH_SHORT).show();
                     break;
                 case 3:
//                     Toast.makeText(InformationActivity.this, "Hiện tại chưa có phiên bản mới!!", Toast.LENGTH_SHORT).show();
                     break;
                 case 4:
//                     Toast.makeText(InformationActivity.this, "Hiện tại chưa có phiên bản mới!!", Toast.LENGTH_SHORT).show();
                     break;

             }
            }
        });
    }

    private void addControls() {
        mListView = (ListView) findViewById(R.id.lvInfor);
        mToolbarInfor =(Toolbar) findViewById(R.id.toolbarInfor);
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
