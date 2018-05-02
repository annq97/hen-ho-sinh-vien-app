package com.santteam.apphenhosinhvien.model;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.santteam.apphenhosinhvien.MainActivity;
import com.santteam.apphenhosinhvien.R;
import com.santteam.apphenhosinhvien.TrangCaNhanActivity;
import com.squareup.picasso.Picasso;

public class ImageFullSCActivity extends AppCompatActivity {
    private DatabaseReference mOnlineDatabase;
    private FirebaseUser mCurrentUser;
    ImageView imgFullScreen,imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_sc);
        addControls();
        addEvens();

    }

    private void addEvens() {

        final Intent intent=getIntent();
        String linkimage=intent.getStringExtra("linkanhdaidien");
        if (linkimage != null){
            Picasso.with(ImageFullSCActivity.this).load(linkimage).into(imgFullScreen);
        }

        //-----------------------------------back
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addControls() {

        imgBack= (ImageView) findViewById(R.id.imgBack);
        imgFullScreen= (ImageView) findViewById(R.id.imgFullScreen);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mOnlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
        if(mCurrentUser != null){
            if(mOnlineDatabase != null) {
                mOnlineDatabase.child("online").setValue(true);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

}
