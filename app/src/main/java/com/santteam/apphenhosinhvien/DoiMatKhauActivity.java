package com.santteam.apphenhosinhvien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoiMatKhauActivity extends AppCompatActivity {

    private EditText edtMatKhauCu,edtMatKhauMoi,edtXacNhanMatKhauMoi;

    private Button btnXacNhan;
    private Toolbar mToolBarDoiMatKhau;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mDoiMatKhauProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);
        addFirebase();
        addControls();
        setSupportActionBar(mToolBarDoiMatKhau);
        getSupportActionBar().setTitle("Đổi mật khẩu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDoiMatKhauProgress = new ProgressDialog(this);
        addEvents();
    }

    private void addEvents() {

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String matKhauCu = edtMatKhauCu.getText().toString();
                final String matKhauMoi = edtMatKhauMoi.getText().toString();
                final String xacNhanMatKhauMoi = edtXacNhanMatKhauMoi.getText().toString();
                final String email = mCurrentUser.getEmail().toString();
                mDoiMatKhauProgress.setTitle("Đang kiểm tra");
                mDoiMatKhauProgress.setMessage("Chúng tôi đang kiểm tra mật khẩu của bạn.");
                mDoiMatKhauProgress.show();
                if(matKhauCu.length() > 0 && matKhauMoi.length() >0 && xacNhanMatKhauMoi.length() > 0) {

                    if (matKhauMoi.equals(xacNhanMatKhauMoi)) {
                        mAuth.signInWithEmailAndPassword(email, matKhauCu).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mCurrentUser.updatePassword(matKhauMoi).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mDoiMatKhauProgress.dismiss();
                                                Toast.makeText(DoiMatKhauActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                mDoiMatKhauProgress.hide();
                                                Toast.makeText(DoiMatKhauActivity.this, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                } else {
                                    mDoiMatKhauProgress.hide();
                                    Toast.makeText(DoiMatKhauActivity.this, "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } else {
                        mDoiMatKhauProgress.hide();
                        Toast.makeText(DoiMatKhauActivity.this, "Mật khẩu mới không trùng khớp!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mDoiMatKhauProgress.hide();
                    Toast.makeText(DoiMatKhauActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addFirebase() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
    }

    private void addControls() {
        edtMatKhauCu = findViewById(R.id.edtMatKhauCu);
        edtMatKhauMoi = findViewById(R.id.edtMatKhauMoi);
        edtXacNhanMatKhauMoi = findViewById(R.id.edtXacNhanMatKhauMoi);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        mToolBarDoiMatKhau = findViewById(R.id.toolBarDoiMatKhau);

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
