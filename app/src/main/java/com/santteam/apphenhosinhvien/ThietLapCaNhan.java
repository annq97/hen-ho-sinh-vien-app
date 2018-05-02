package com.santteam.apphenhosinhvien;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ThietLapCaNhan extends AppCompatActivity {
    private RadioButton rbNam,rbNu;
    private CheckBox chkNgheNhac, chkTheThao, chkMiThuat,chkToanHoc,chkThienVanHoc,chkDuLich;
    private Button btnHuy , btnOk ;
    private EditText edtHocTruong,edtKhac,mUsername;
    private Button btnChonNgay;
    private TextView tvNgaySinh;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thiet_lap_ca_nhan);
        addFireBase();
        addControls();
        addEvens();
    }

    private void addFireBase() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mCurrentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsername.setText(dataSnapshot.child("username").getValue().toString());
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

    private  void  addEvens (){
        btnChonNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                int year=calendar.get(Calendar.YEAR);
                int month=calendar.get(Calendar.MONTH);
                int day=calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener onDateSetListener= new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        tvNgaySinh.setText(i2+"/"+(i1 + 1)+"/"+i);
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(ThietLapCaNhan.this,onDateSetListener,year,month,day);
                dialog.show();
            }
        });
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gioitinh = "";
                String truong = "";
                String ngaysinh = "";
                String sothich = "";
                String username = "";
                    if (rbNu.isChecked()) gioitinh += "Nữ";
                    if (rbNam.isChecked()) gioitinh += "Nam";
                    if (gioitinh.compareTo("") != 0) {
                        databaseReference.child("gioitinh").setValue(gioitinh);
                    }
                    if (tvNgaySinh.getText().toString().compareTo("") != 0) {
                        databaseReference.child("ngaysinh").setValue(tvNgaySinh.getText().toString());
                    }
                    if (edtHocTruong.getText().toString().compareTo("") != 0) {
                        databaseReference.child("truong").setValue(edtHocTruong.getText().toString());
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
                    if (sothich.compareTo("") != 0) {
                        databaseReference.child("sothich").setValue(sothich);
                    }

                    if(!mUsername.getText().toString().equals("")){
                        databaseReference.child("username").setValue(mUsername.getText().toString());
                    }
                    Toast.makeText(ThietLapCaNhan.this, "Thay đổi thành công!", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(ThietLapCaNhan.this, MainActivity.class);
                    startActivity(mainIntent);
            }
        });
    }
    private  void addControls (){
        rbNam = (RadioButton) findViewById(R.id.rbNam);
        rbNu= (RadioButton) findViewById(R.id.rbNu);
        btnHuy= (Button) findViewById(R.id.btnHuy);
        btnOk =(Button) findViewById(R.id.btnOk);
        btnChonNgay =(Button) findViewById(R.id.btnChonNgay);
        edtHocTruong = (EditText) findViewById(R.id.edtHocTruong);
        edtKhac = (EditText) findViewById(R.id.edtKhac);
        tvNgaySinh = (TextView) findViewById(R.id.tvNgaySinh);
        chkDuLich= (CheckBox) findViewById(R.id.chkDuLich);
        chkMiThuat= (CheckBox) findViewById(R.id.chkMiThuat);
        chkNgheNhac= (CheckBox) findViewById(R.id.chkNgheNhac);
        chkTheThao= (CheckBox) findViewById(R.id.chkTheThao);
        chkThienVanHoc= (CheckBox) findViewById(R.id.chkThienVanHoc);
        chkToanHoc= (CheckBox) findViewById(R.id.chkToanHoc);
        mUsername = findViewById(R.id.edtUsername);
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
