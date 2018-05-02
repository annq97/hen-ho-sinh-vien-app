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
import com.santteam.apphenhosinhvien.model.KhachHang;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DangKyActivity extends AppCompatActivity {
    Button btnDangKy,btnChonNgay;
    TextView tvNgaySinh;
    EditText edtHocTruong,edtTenDangKi,edtMatKhau,edtNhapLaiMK,edtKhac,edtEmail;
    RadioButton rbNam,rbNu;
    CheckBox chkNgheNhac,chkTheThao,chkMiThuat,chkToanHoc,chkThienVanHoc,chkDuLich;
    ImageButton imgBtnBack;
    private Toolbar mtoolbarDK;

    //Firebase Auth
    private FirebaseAuth mAuth ;

    //Firebase Database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference mOnlineDatabase;
    //Progress
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        addFireBase();
        addControls();
        addEvens();
        setSupportActionBar(mtoolbarDK);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void addFireBase() {
        //Auth
        mAuth = FirebaseAuth.getInstance();
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
                DatePickerDialog dialog=new DatePickerDialog(DangKyActivity.this,onDateSetListener,year,month,day);
                dialog.show();
            }
        });
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtEmail.getText().toString().compareTo("")==0 || edtTenDangKi.getText().toString().compareTo("")==0 ||
                        edtMatKhau.getText().toString().compareTo("")==0 ||
                        edtNhapLaiMK.getText().toString().compareTo("")==0 ||
                        edtHocTruong.getText().toString().compareTo("")==0 )
                    Toast.makeText(DangKyActivity.this,"Vui long nhập đầy đủ thông tin!",Toast.LENGTH_SHORT).show();
                else if(edtMatKhau.getText().toString().compareTo(edtNhapLaiMK.getText().toString())!=0) {
                    Toast.makeText(DangKyActivity.this,"Mật khẩu không trùng khớp!",Toast.LENGTH_SHORT).show();
                }
                else if(tvNgaySinh.getText().toString().equals("") || kiemTraTuoi(tvNgaySinh.getText().toString(),year)==false){
                    Toast.makeText(DangKyActivity.this,"Nhập đúng năm sinh!!",Toast.LENGTH_SHORT).show();
                }
                else {
                    String gioiTinh="";
                    if(rbNu.isChecked()) gioiTinh+="Nữ";
                    else gioiTinh+="Nam";
                    String sothich="";
                    switch (1){
                        case 1:if(chkDuLich.isChecked()) sothich+=chkDuLich.getText();
                        case 2:if(chkMiThuat.isChecked()) sothich+="\n"+chkMiThuat.getText();
                        case 3:if(chkToanHoc.isChecked()) sothich+="\n"+chkToanHoc.getText().toString();
                        case 4:if(chkThienVanHoc.isChecked()) sothich+="\n"+chkThienVanHoc.getText().toString();
                        case 5:if(chkTheThao.isChecked()) sothich+="\n"+chkTheThao.getText().toString();
                        case 6:if(chkNgheNhac.isChecked()) sothich+="\n"+chkNgheNhac.getText().toString();
                    }
                    String email=edtEmail.getText().toString();
                    String tenDangNhap=edtTenDangKi.getText().toString();
                    String matKhau=edtMatKhau.getText().toString();
                    String hocTruong=edtHocTruong.getText().toString();
                    String ngaySinh=tvNgaySinh.getText().toString();

                    mRegProgress.setTitle("Đăng kí tài khoản");
                    mRegProgress.setMessage("Đợi 1 tý. Chúng tôi đang tạo tài khoản cho bạn !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    dangKi_User(email,tenDangNhap,matKhau,gioiTinh,ngaySinh,hocTruong,sothich);
                }
            }
        });
    }

    private void dangKi_User(final String email, final String tenDangNhap, final String matKhau, final String gioiTinh, final String ngaySinh, final String hocTruong, final String sothich) {
        mAuth.createUserWithEmailAndPassword(email,matKhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String userID = current_user.getUid();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference().child("users").child(userID);

                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("username",tenDangNhap);
                    userMap.put("gioitinh",gioiTinh);
                    userMap.put("ngaysinh",ngaySinh);
                    userMap.put("truong",hocTruong);
                    userMap.put("sothich",sothich);
                    userMap.put("anhdaidien","t.com/o/anhdaidien%2Fimg_add_default.png?alt=media&token=928dbf0a-6013-4af5-bffd-d2e5e80d9b79");
                    userMap.put("ID",userID);

                    databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                Intent dangNhapIntent = new Intent(DangKyActivity.this,DangNhapActivity.class);
                                dangNhapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                dangNhapIntent.putExtra("Email", email);
                                dangNhapIntent.putExtra("Mat Khau", matKhau);

                                startActivity(dangNhapIntent);
                                finish();
                            }
                        }
                    });
                }else{
                    mRegProgress.hide();
                    Toast.makeText(DangKyActivity.this,"Tạo tài khoản không thành công. Vui lòng kiểm tra lại thông tin!",Toast.LENGTH_SHORT).show();
                }
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
        btnDangKy= (Button) findViewById(R.id.btnDangKy);
        edtTenDangKi= (EditText) findViewById(R.id.edtTenDangKi);
        edtMatKhau= (EditText) findViewById(R.id.edtMatKhau);
        edtNhapLaiMK= (EditText) findViewById(R.id.edtNhapLaiMK);
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
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        mRegProgress = new ProgressDialog(DangKyActivity.this);

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
