package com.santteam.apphenhosinhvien;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FriendActivity extends AppCompatActivity {
    private static final String TAG = FriendActivity.class.getName();
    private Toolbar mToolBarFriend;
    private TabLayout mTabLayoutFriend;
    private ViewPager mViewPagerFriend;
    private MainSectionsPagerAdapter mMainSectionsPagerAdapter;
    private DatabaseReference mOnlineDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            mOnlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
        }
        addControls();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addControls() {
        mToolBarFriend = findViewById(R.id.toolBarFriend);
        mTabLayoutFriend = findViewById(R.id.tabLayoutFriend);
        mViewPagerFriend = findViewById(R.id.viewPagerFriend);

        setSupportActionBar(mToolBarFriend);
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMainSectionsPagerAdapter = new MainSectionsPagerAdapter(getSupportFragmentManager(), "FRIENDS", "REQUESTS FRIEND");
        mViewPagerFriend.setAdapter(mMainSectionsPagerAdapter);

        mTabLayoutFriend.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        mTabLayoutFriend.setupWithViewPager(mViewPagerFriend);
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
