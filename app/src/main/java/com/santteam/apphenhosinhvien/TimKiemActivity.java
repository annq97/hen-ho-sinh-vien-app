package com.santteam.apphenhosinhvien;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
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
import com.google.firebase.database.ValueEventListener;
import com.santteam.apphenhosinhvien.model.KhachHang;
import com.santteam.apphenhosinhvien.model.SoThich;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;


public class TimKiemActivity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTimKiem;
    private RecyclerView rvUser;
    private ImageButton imgBtnBack, imgBtnSearch, imgBtnLoc;
    private String mTuKhoa, gioitinhAdmin = "";
    private DatabaseReference databaseReference;
    private FirebaseUser mCurrentUser;
    private ArrayList<KhachHang> danhSachTimKiems = new ArrayList<>();
    private ArrayList<KhachHang> khachHangs;
    private ArrayList<KhachHang> danhSachKhachHangs = new ArrayList<>();

    private ArrayList<String> soThichs = new ArrayList<>();
    private String tuoi;
    private String gioiTinh;

    private RecyclerAdapterKhachHang recyclerAdapterKhachHang;
    private Toolbar mToolbarTimKiem;

    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_kiem);
        Intent intent = getIntent();
        if (intent != null) {
            soThichs = intent.getStringArrayListExtra("sothich");

            gioiTinh = intent.getStringExtra("gioitinh");
            tuoi = intent.getStringExtra("tuoi");



            if (soThichs == null) {
                soThichs = new ArrayList<>();
            }
        }
        addControls();
        addFirebase();




        if (danhSachTimKiems.size() == 0) {
            recyclerAdapterKhachHang = new RecyclerAdapterKhachHang(khachHangs, TimKiemActivity.this);
            rvUser.setAdapter(recyclerAdapterKhachHang);
        }
        addEvents();
        mToolbarTimKiem.setTitle("Tìm Kiếm");
        setSupportActionBar(mToolbarTimKiem);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void addFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        khachHangs = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                danhSachKhachHangs.clear();
                for(DataSnapshot dt : dataSnapshot.getChildren()){
                    danhSachKhachHangs.add(dt.getValue(KhachHang.class));
                }

                for(KhachHang kh : danhSachKhachHangs) {
                    if (kh.getID().equals(mCurrentUser.getUid().toString())) {
                        gioitinhAdmin = kh.getGioitinh().toString();
                        break;
                    }

                }

                if(tuoi  ==  null && gioiTinh == null){
                    for(KhachHang kh : danhSachKhachHangs) {

                        if (gioitinhAdmin.equals(kh.getGioitinh()) == false) {
                            if (soThichs.size() == 0) {
                                if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                            } else
                            {
                                int dem = 0;
                                for (String st : soThichs) {
                                    if (kh.getSothich().contains(st)) {
                                        dem++;
                                    }
                                }
                                if (dem > 0) {
                                    if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                                }
                            }
                        }
                        if(khachHangs.size() != 0) {
                            long seed = System.nanoTime();
                            Collections.shuffle(khachHangs, new Random(seed));
                        }

                    }
                }
                if(tuoi != null && gioiTinh == null){
                    for(KhachHang kh : danhSachKhachHangs) {

                        int tuoiUsers = 0;
                        if(kh.getNgaysinh().compareTo("") != 0) {
                            String[] nam = kh.getNgaysinh().split("/");
                            tuoiUsers = year - Integer.parseInt(nam[2]);
                        }

                        if (gioitinhAdmin.equals(kh.getGioitinh()) == false) {
                            if (soThichs.size() == 0 && tuoi.compareTo(""+tuoiUsers) == 0) {
                                if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                            } else
                            {
                                int dem = 0;
                                for (String st : soThichs) {
                                    if (kh.getSothich().contains(st)) {
                                        dem++;
                                    }
                                }

                                if (dem > 0 && tuoi.compareTo(""+tuoiUsers) == 0 ) {
                                    if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                                }
                            }
                        }
                        if(khachHangs.size() != 0) {
                            long seed = System.nanoTime();
                            Collections.shuffle(khachHangs, new Random(seed));
                        }
                    }
                }
                if(tuoi == null && gioiTinh != null){
                    for(KhachHang kh : danhSachKhachHangs) {


                        if (soThichs.size() == 0 && gioiTinh.contains(""+kh.getGioitinh()) == true) {
                            if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                        } else
                        {
                            int dem = 0;
                            for (String st : soThichs) {
                                if (kh.getSothich().contains(st)) {
                                    dem++;
                                }
                            }


                            if (dem > 0 && gioiTinh.contains(""+kh.getGioitinh()) == true ) {
                                if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                            }
                        }

                        if(khachHangs.size() != 0) {
                            long seed = System.nanoTime();
                            Collections.shuffle(khachHangs, new Random(seed));
                        }
                    }
                }
                if(tuoi != null && gioiTinh != null){
                    for(KhachHang kh : danhSachKhachHangs) {

                        int tuoiUsers = 0;
                        if(kh.getNgaysinh().compareTo("") != 0) {
                            String[] nam = kh.getNgaysinh().split("/");
                            tuoiUsers = year - Integer.parseInt(nam[2]);
                        }

                        if (soThichs.size() == 0 && gioiTinh.contains(""+kh.getGioitinh()) == true &&
                                tuoi.compareTo(""+tuoiUsers) == 0) {
                            if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                        } else
                        {
                            int dem = 0;
                            for (String st : soThichs) {
                                if (kh.getSothich().contains(st)) {
                                    dem++;
                                }
                            }



                            if (dem > 0 && gioiTinh.contains(""+kh.getGioitinh()) == true &&
                                    tuoi.compareTo(""+tuoiUsers) == 0) {
                                if (kh.getID().equals(mCurrentUser.getUid().toString()) == false) khachHangs.add(kh);
                            }
                        }
                        if(khachHangs.size() != 0) {
                            long seed = System.nanoTime();
                            Collections.shuffle(khachHangs, new Random(seed));
                        }
                    }
                }

                if(khachHangs.size() == 0) Toast.makeText(TimKiemActivity.this, "Không tìm thấy đối tượng mà bạn muốn", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addEvents() {

        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                danhSachTimKiems.clear();
                mTuKhoa = autoCompleteTimKiem.getText().toString().trim();
                for (KhachHang kh : khachHangs) {
                    if (kh.getUsername().toLowerCase().indexOf(mTuKhoa.toLowerCase()) == 0) {
                        danhSachTimKiems.add(kh);
                    }
                }
                recyclerAdapterKhachHang = new RecyclerAdapterKhachHang(danhSachTimKiems, TimKiemActivity.this);
                rvUser.setAdapter(recyclerAdapterKhachHang);
            }
        });

        imgBtnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimKiemActivity.this, LocActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addControls() {
        mToolbarTimKiem = (Toolbar) findViewById(R.id.toolBarTimKiem);
        imgBtnSearch = (ImageButton) findViewById(R.id.imgBtnSearch);
        imgBtnLoc = (ImageButton) findViewById(R.id.imgBtnLoc);
        rvUser = (RecyclerView) findViewById(R.id.rvUser);
        rvUser.setHasFixedSize(true);
        rvUser.setLayoutManager(new GridLayoutManager(this, 3));
        autoCompleteTimKiem = (AutoCompleteTextView) findViewById(R.id.autoCompleteTimKiem);
    }

    public void chuyenMH(String ID) {
        Intent trangCaNhanIntent = new Intent(TimKiemActivity.this, TrangCaNhanActivity.class);
        trangCaNhanIntent.putExtra("ID", ID);
        startActivity(trangCaNhanIntent);

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
