package com.santteam.apphenhosinhvien;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NaviHeaderActivity extends AppCompatActivity {
    private TextView mUsername;
    private CircleImageView mAnhDaiDien;
    private DatabaseReference mUsersDatabase;
    private FirebaseUser mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_header);
        addControls();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUsersDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsername.setText(dataSnapshot.child("username").getValue().toString());
                Picasso.with(NaviHeaderActivity.this).load(dataSnapshot.child("anhdaidien").getValue().toString()).placeholder(R.drawable.img_add_default).into(mAnhDaiDien);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addControls() {
        mUsername = findViewById(R.id.tvUsername);
        mAnhDaiDien = findViewById(R.id.imgAnhDaiDien);
    }
}
