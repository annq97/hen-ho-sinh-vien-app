package com.santteam.apphenhosinhvien;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class CheckThongTinDangNhapActivity extends AppCompatActivity {
    Button mXacNhan,mHuyBo,btnChonNgay;
    TextView tvNgaySinh;
    EditText edtHocTruong,edtUsername,edtKhac;
    RadioButton rbNam,rbNu;
    CheckBox chkNgheNhac,chkTheThao,chkMiThuat,chkToanHoc,chkThienVanHoc,chkDuLich;
    private Toolbar mtoolbarDK;

    //Firebase Auth
    private FirebaseAuth mAuth ;
    private FirebaseUser mCurrentUser;
    //Firebase Database
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_thong_tin_dang_nhap);
        addControls();
        addFireBase();
        addEvens();
        setSupportActionBar(mtoolbarDK);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void addFireBase() {
        //Auth
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                edtUsername.setText(dataSnapshot.child("username").getValue().toString());
                if(dataSnapshot.child("gioitinh").getValue().equals("Nam")){
                    rbNam.setChecked(true);
                }
                else{
                    rbNu.setChecked(true);
                }

                tvNgaySinh.setText(dataSnapshot.child("ngaysinh").getValue().toString());
                edtHocTruong.setText(dataSnapshot.child("truong").getValue().toString());

                if(dataSnapshot.child("sothich").getValue().toString().contains(chkNgheNhac.getText().toString())){
                    chkNgheNhac.setChecked(true);
                }
                if(dataSnapshot.child("sothich").getValue().toString().contains(chkDuLich.getText().toString())){
                    chkDuLich.setChecked(true);
                }
                if(dataSnapshot.child("sothich").getValue().toString().contains(chkMiThuat.getText().toString())){
                    chkMiThuat.setChecked(true);
                }
                if(dataSnapshot.child("sothich").getValue().toString().contains(chkTheThao.getText().toString())){
                    chkTheThao.setChecked(true);
                }
                if(dataSnapshot.child("sothich").getValue().toString().contains(chkThienVanHoc.getText().toString())){
                    chkThienVanHoc.setChecked(true);
                }
                if(dataSnapshot.child("sothich").getValue().toString().contains(chkToanHoc.getText().toString())){
                    chkToanHoc.setChecked(true);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addEvens() {
        final Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        btnChonNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener onDateSetListener= new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tvNgaySinh.setText(i2+"/"+(i1 + 1)+"/"+i);
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(CheckThongTinDangNhapActivity.this,onDateSetListener,year,month,day);
                dialog.show();
            }
        });
        mXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String gioitinh = "";
                String sothich = "";

                if (rbNu.isChecked()) gioitinh += "Nữ";
                if (rbNam.isChecked()) gioitinh += "Nam";
                if (gioitinh.trim().compareTo("") != 0) {
                    mUserDatabase.child(mCurrentUser.getUid()).child("gioitinh").setValue(gioitinh.trim());
                }
                if (tvNgaySinh.getText().toString().trim().compareTo("") != 0) {
                    mUserDatabase.child(mCurrentUser.getUid()).child("ngaysinh").setValue(tvNgaySinh.getText().toString().trim());
                }
                if (edtHocTruong.getText().toString().trim().compareTo("") != 0) {
                    mUserDatabase.child(mCurrentUser.getUid()).child("truong").setValue(edtHocTruong.getText().toString().trim());
                }
                switch (1) {
                    case 1:
                        if (chkDuLich.isChecked()) sothich += chkDuLich.getText();
                    case 2:
                        if (chkMiThuat.isChecked())
                            sothich += "\n" + chkMiThuat.getText();
                    case 3:
                        if (chkToanHoc.isChecked())
                            sothich += "\n" + chkToanHoc.getText().toString();
                    case 4:
                        if (chkThienVanHoc.isChecked())
                            sothich += "\n" + chkThienVanHoc.getText().toString();
                    case 5:
                        if (chkTheThao.isChecked())
                            sothich += "\n" + chkTheThao.getText().toString();
                    case 6:
                        if (chkNgheNhac.isChecked())
                            sothich += "\n" + chkNgheNhac.getText().toString();
                }
                if (sothich.trim().compareTo("") != 0) {
                    mUserDatabase.child(mCurrentUser.getUid()).child("sothich").setValue(sothich.trim());
                }

                if(!edtUsername.getText().toString().trim().equals("")){
                    mUserDatabase.child(mCurrentUser.getUid()).child("username").setValue(edtUsername.getText().toString().trim());
                }

                if(!edtUsername.getText().toString().trim().equals("") && !gioitinh.trim().equals("")
                        && !sothich.trim().equals("") && !tvNgaySinh.getText().toString().trim().equals("") && !edtHocTruong.getText().toString().trim().equals("") ) {

                    Toast.makeText(CheckThongTinDangNhapActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(CheckThongTinDangNhapActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else{
                    Toast.makeText(CheckThongTinDangNhapActivity.this, "Vui lòng cập nhật đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mHuyBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserDatabase.child(mCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                           mCurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(CheckThongTinDangNhapActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                        Intent dangNhapIntent = new Intent(CheckThongTinDangNhapActivity.this,DangNhapActivity.class);
                                        startActivity(dangNhapIntent);
                                        finish();
                                    }
                               }
                           });
                        }
                    }
                });
            }
        });
    }

    private boolean kiemTraTuoi(String ngaySinh,int year) {
        String[]namSinh=ngaySinh.split("/");
        if(year-Integer.parseInt(namSinh[2])<=28 && year-Integer.parseInt(namSinh[2])>=17) return true;
        return false;
    }



    private void addControls() {
        mtoolbarDK = (Toolbar) findViewById(R.id.toolBarDangKi);
        tvNgaySinh= (TextView) findViewById(R.id.tvNgaySinh);
        btnChonNgay= (Button) findViewById(R.id.btnChonNgay);
        edtHocTruong= (EditText) findViewById(R.id.edtHocTruong);
        rbNam= (RadioButton) findViewById(R.id.rbNam);
        rbNu= (RadioButton) findViewById(R.id.rbNu);
        edtKhac= (EditText) findViewById(R.id.edtKhac);
        chkDuLich= (CheckBox) findViewById(R.id.chkDuLich);
        chkMiThuat= (CheckBox) findViewById(R.id.chkMiThuat);
        chkNgheNhac= (CheckBox) findViewById(R.id.chkNgheNhac);
        chkTheThao= (CheckBox) findViewById(R.id.chkTheThao);
        chkThienVanHoc= (CheckBox) findViewById(R.id.chkThienVanHoc);
        chkToanHoc= (CheckBox) findViewById(R.id.chkToanHoc);
        edtUsername = findViewById(R.id.edtUsername);
        mXacNhan = findViewById(R.id.btnXacNhanCapNhat);
        mHuyBo = findViewById(R.id.btnHuyBoCapNhat);

    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
        }
        else { Toast.makeText(getBaseContext(), "Không sử dụng được chức năng này!", Toast.LENGTH_SHORT).show(); }

    }
}
