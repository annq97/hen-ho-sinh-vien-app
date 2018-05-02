package com.santteam.apphenhosinhvien;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class QuenMatKhauActivity extends AppCompatActivity {

    private TextView mEmail;
    private Button mXacNhan;
    private Toolbar mToolBarQuenMatKhau;
    private ProgressDialog mProgressXacNhan;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quen_mat_khau);
        addControls();
        setSupportActionBar(mToolBarQuenMatKhau);
        getSupportActionBar().setTitle("Quên Mật Khẩu");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        addEvents();
    }

    private void addEvents() {
        mXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressXacNhan = new ProgressDialog(QuenMatKhauActivity.this);
                mProgressXacNhan.setTitle("Xác thực email");
                mProgressXacNhan.setMessage("Chúng tôi đang xác thực email của bạn. Hãy kiễm tra hộp thư email sau vài giây");
                mProgressXacNhan.show();
                String email = mEmail.getText().toString();
                if(!email.equals("")) {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mProgressXacNhan.dismiss();
                                Toast.makeText(QuenMatKhauActivity.this, "Reset thành công. Vui lòng check hộp thư email của bạn!", Toast.LENGTH_SHORT).show();
                            } else {
                                mProgressXacNhan.hide();
                                Toast.makeText(QuenMatKhauActivity.this, "Reset mật khẩu không thành công!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    mProgressXacNhan.hide();
                    Toast.makeText(QuenMatKhauActivity.this, "Vui lòng nhập vào địa chỉ email của bạn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addControls() {
        mEmail = findViewById(R.id.edtEmail);
        mXacNhan = findViewById(R.id.btnXacNhan);
        mToolBarQuenMatKhau = findViewById(R.id.toolBarQuenMatKhau);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
