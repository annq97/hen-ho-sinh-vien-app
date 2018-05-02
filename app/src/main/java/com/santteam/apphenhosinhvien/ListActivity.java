package com.santteam.apphenhosinhvien;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListActivity extends AppCompatActivity {
    private Toolbar mToolBarMain;
    private TabLayout mTabLayoutMain;
    private ViewPager mViewPagerMain;
    private MainSectionsPagerAdapter mMainSectionsPagerAdapter;
    private DatabaseReference mOnlineDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        addControls();
        addFirebase();

    }

    private void addFirebase() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mOnlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addControls() {
        mToolBarMain = findViewById(R.id.toolBarMain);
        mTabLayoutMain = findViewById(R.id.tabLayoutMain);
        mViewPagerMain = findViewById(R.id.viewPagerMain);

        setSupportActionBar(mToolBarMain);
        getSupportActionBar().setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMainSectionsPagerAdapter = new MainSectionsPagerAdapter(getSupportFragmentManager(),"CHATS","REQUESTS CHAT");
        mViewPagerMain.setAdapter(mMainSectionsPagerAdapter);

        mTabLayoutMain.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        mTabLayoutMain.setupWithViewPager(mViewPagerMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
