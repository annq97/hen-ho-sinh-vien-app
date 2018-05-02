package com.santteam.apphenhosinhvien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.santteam.apphenhosinhvien.model.KhachHang;
import com.santteam.apphenhosinhvien.model.SoThich;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageButton imgBtnKetBan, imgBtnYeuThich, imgBtnDanhSach, imgBtnTinNhan, imgBtnTimKiem, imgBtnMenu;
    private ImageView imvAnhDaiDien;
    private TextView tvTen;

    //Database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference mUsersDatabase;
    private FirebaseUser currentUser;
    private DatabaseReference mOnlineDatabase;
    private DatabaseReference mLikesDatabase;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbarM;

    private String gioitinh = "";
    private String st = "";
    private ArrayList<KhachHang> khachHangs;
    private ProgressDialog mKetBanProgress;
    private ArrayList<KhachHang> danhSachKhachHangs = new ArrayList<>();
    private int check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent startIntent = new Intent(MainActivity.this, ManHinhChaoActivity.class);
            startActivity(startIntent);
            finish();
        } else {
            if(CheckLogin.Instance().getCheck() == 1) {
                final ProgressDialog mThongBaoProgress = new ProgressDialog(this);
                mThongBaoProgress.setTitle("Nhận Dữ Liệu");
                mThongBaoProgress.setMessage("Chúng tôi đang nhận thông tin về tài khoản của bạn. Vui lòng đợi trong giây lát!");
                mThongBaoProgress.setCanceledOnTouchOutside(false);
                mThongBaoProgress.show();
                CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        mThongBaoProgress.dismiss();
                    }
                };
                countDownTimer.start();
            }
            CheckLogin.Instance().setCheck(2);
            mOnlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        }
        addFirebase();
        addControls();
        addEvens();

    }

    private void addFirebase() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("users").child(userID);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tvTen.setText(dataSnapshot.child("username").getValue().toString());
                    if (!dataSnapshot.child("anhdaidien").getValue().equals("default")) {
                        Picasso.with(MainActivity.this).load(dataSnapshot.child("anhdaidien").getValue().toString()).placeholder(R.drawable.img_add_default).into(imvAnhDaiDien);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void addEvens() {

        imgBtnTinNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(chatIntent);
            }
        });
        imgBtnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MainActivity.this, TimKiemActivity.class);
                startActivity(searchIntent);
            }
        });
        imvAnhDaiDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageProfileIntent = new Intent(MainActivity.this, TrangCaNhanActivity.class);
                startActivity(imageProfileIntent);
            }
        });
        imgBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.END);

            }
        });

        imgBtnDanhSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendIntent = new Intent(MainActivity.this, FriendActivity.class);
                startActivity(friendIntent);
            }
        });
        imgBtnKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKetBanProgress = new ProgressDialog(MainActivity.this);
                mKetBanProgress.setTitle("Kết nối ngẫu nhiên");
                mKetBanProgress.setMessage("Chúng tôi đang tìm đối tượng phù hợp với bạn. Vui lòng đợi trong giây lát!");
                mKetBanProgress.setCanceledOnTouchOutside(false);
                mKetBanProgress.show();
                khachHangs = new ArrayList<>();
                ketNoiNgauNhien();

            }
        });
        NavigationView mnaviMain = (NavigationView) findViewById(R.id.navigationMain);
        mnaviMain.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuDoiMatKhau:
                        Intent intent=new Intent(MainActivity.this,DoiMatKhauActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.mnuCanhan:
                        Intent imageProfileIntent = new Intent(MainActivity.this, TrangCaNhanActivity.class);
                        startActivity(imageProfileIntent);
                        break;
                    case R.id.mnuSetting:
                        Intent sintent = new Intent(MainActivity.this, ThietLapCaNhan.class);
                        startActivity(sintent);
                        break;
                    case R.id.mnuDangXuat:
                        if (mOnlineDatabase != null) {
                            mOnlineDatabase.child("online").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent chaoIntent = new Intent(MainActivity.this, DangNhapActivity.class);
                                        startActivity(chaoIntent);
                                        finish();
                                    }
                                }
                            });

                        }
                        break;
                    case R.id.mnuInfomation:
                        Intent intentInfor = new Intent(MainActivity.this, InformationActivity.class);
                        startActivity(intentInfor);
                        break;
                    case R.id.mnuShare:
                        Intent shareIntent = new Intent();
                        String sharett = "I love it";
                        shareIntent.setAction(Intent.ACTION_SEND);

                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, sharett);
                        startActivity(Intent.createChooser(shareIntent, "Chia sẻ với bạn bè ngay nào!"));
                        break;
                    case R.id.mnuLienHe:
                        break;

                }
                return true;
            }
        });

        imgBtnYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLikesDatabase = FirebaseDatabase.getInstance().getReference().child("likes");
                mLikesDatabase.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot dt : dataSnapshot.getChildren()){
                            if(dt.getValue() != null){
                                Intent chatIntent = new Intent(MainActivity.this,ChatActivity.class);
                                chatIntent.putExtra("ID",dt.getKey());
                                startActivity(chatIntent);
                            }else{
                                Toast.makeText(MainActivity.this, "Chưa có ai đã kết nối với bạn!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
               check = 0;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                check = 1;

            }
        };


        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void ketNoiNgauNhien() {

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                danhSachKhachHangs.clear();
                for(DataSnapshot dt : dataSnapshot.getChildren()){
                    KhachHang khachHang = dt.getValue(KhachHang.class);
                    //khachHang.setOnline("true");
                    danhSachKhachHangs.add(khachHang);

                }

                for(KhachHang kh : danhSachKhachHangs) {
                    if (kh.getID().equals(currentUser.getUid().toString())) {
                        gioitinh = kh.getGioitinh().toString();
                        st = kh.getSothich();
                        break;
                    }

                }


                for(KhachHang kh : danhSachKhachHangs) {

                    if (!gioitinh.equals(kh.getGioitinh())) {
                        if(kh.getOnline().equals("true")) {
                            if (st.indexOf("\n") != -1) {
                                String[] soThichs = st.split("\n");
                                int dem = 0;
                                for (String st1 : soThichs) {
                                    if (kh.getSothich().contains(st1)) {
                                        dem++;
                                    }
                                }
                                if (dem > 0) {
                                    khachHangs.add(kh);
                                }
                            } else {
                                if (kh.getSothich().contains(st)) {
                                    khachHangs.add(kh);
                                }
                            }
                        }
                    }
                    }

                    long seed = System.nanoTime();
                    Collections.shuffle(khachHangs, new Random(seed));
                    if(khachHangs.size() > 0){
                        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                mKetBanProgress.dismiss();

                                Intent chatIntent = new Intent(MainActivity.this,ChatActivity.class);
                                chatIntent.putExtra("ID",khachHangs.get(0).getID());
                                startActivity(chatIntent);
                            }
                        };
                        countDownTimer.start();
                    }else{

                        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                mKetBanProgress.hide();
                                Toast.makeText(MainActivity.this, "Không tìm thấy đối tượng phù hợp!", Toast.LENGTH_SHORT).show();
                            }
                        };
                        countDownTimer.start();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void addControls() {
        imgBtnKetBan = (ImageButton) findViewById(R.id.imgBtnKetBan);
        imgBtnYeuThich = (ImageButton) findViewById(R.id.imgBtnYeuThich);
        imgBtnDanhSach = (ImageButton) findViewById(R.id.imgBtnDanhSach);
        imgBtnTinNhan = (ImageButton) findViewById(R.id.imgBtnTinNhan);
        imgBtnTimKiem = (ImageButton) findViewById(R.id.imgBtnTimKiem);
        imvAnhDaiDien = (ImageView) findViewById(R.id.imgAnhDaiDien);
        imgBtnMenu = (ImageButton) findViewById(R.id.imgBtnMenu);
        tvTen = (TextView) findViewById(R.id.tvTen);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoutMain);
        mToolbarM = findViewById(R.id.toolBarMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mOnlineDatabase != null) {
            mOnlineDatabase.child("online").setValue("true");

        }

    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if(check == 0) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(getBaseContext(), "Nhấn thêm lần nữa để thoát!", Toast.LENGTH_SHORT).show();
            }

            mBackPressed = System.currentTimeMillis();
        }else{
            mDrawerLayout.closeDrawer(GravityCompat.END);
            check =0;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOnlineDatabase != null) {
            mOnlineDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
}
