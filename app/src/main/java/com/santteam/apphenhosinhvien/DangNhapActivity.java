package com.santteam.apphenhosinhvien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.HashMap;

public class DangNhapActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener,GoogleApiClient.OnConnectionFailedListener{

    private EditText edtMatKhau,edtTenDangNhap;
    private TextView tvDangKy,tvQuenMatKhau;
    private Button btnDangNhap;
    private FirebaseAuth mAuth ;
    private FirebaseUser mCurrentUser;
    private SignInButton mDangNhapGoogle;
    private LoginButton mDangNhapFacebook;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mUserDatabase;
    private HashMap<String,String> userMap;
    private String username = "";
    private String gioitinh = "";
    private String ngaysinh = "";
    private String truong = "";
    private String sothich = "";
    private String userID = "";
    private String anhdaidien = "";

    private CallbackManager callbackManager;

    //Progress
    private ProgressDialog mLoginProgress;
    private ProgressDialog mLoginGoogleProgress;
    private ProgressDialog mLoginFacebookProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        addControls();
        mAuth= FirebaseAuth.getInstance();


        //Đăng nhập google

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
             .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        //Đăng nhập facebook



        addEvents();
    }


    private void addEvents() {
        //----------Inten Sang Man Hinh Dang Ky---------
        tvDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DangNhapActivity.this,DangKyActivity.class);
                startActivity(intent);
            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckLogin.Instance().setCheck(2);
                String email = edtTenDangNhap.getText().toString();
                String password = edtMatKhau.getText().toString();
                if(edtTenDangNhap.length()==0 || edtMatKhau.length()==0)
                    Toast.makeText(DangNhapActivity.this,"Nhập Thiếu Kìa !!",Toast.LENGTH_SHORT).show();
                else{
                    mLoginProgress.setTitle("Đang đăng nhập");
                    mLoginProgress.setMessage("Chúng tôi đang kiểm tra thông tin của bạn");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    login_user(email,password);
                }
            }
        });
        tvQuenMatKhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quenMKIntent = new Intent(DangNhapActivity.this,QuenMatKhauActivity.class);
                startActivity(quenMKIntent);
            }
        });

        mDangNhapGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dangNhapGoogle = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(dangNhapGoogle,3);
            }
        });

        mDangNhapFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mLoginProgress.dismiss();

                    Intent mainIntent = new Intent(DangNhapActivity.this,MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();

                }else{
                    mLoginProgress.hide();
                    Toast.makeText(DangNhapActivity.this,"Đăng nhập không thành công. Vui lòng kiểm tra lại thông tin!",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void addControls() {
        edtMatKhau= (EditText) findViewById(R.id.edtMatKhau);
        edtTenDangNhap= (EditText) findViewById(R.id.edtTenDangNhap);
        tvDangKy= (TextView) findViewById(R.id.tvDangKy);
        btnDangNhap= (Button) findViewById(R.id.btnDangNhap);
        mLoginProgress = new ProgressDialog(DangNhapActivity.this);
        mLoginGoogleProgress = new ProgressDialog(DangNhapActivity.this);
        mLoginFacebookProgress = new ProgressDialog(DangNhapActivity.this);
        tvQuenMatKhau = findViewById(R.id.tvQuenMatKhau);
        mDangNhapGoogle = findViewById(R.id.btnDangNhapGoogle);

        callbackManager = CallbackManager.Factory.create();
        mDangNhapFacebook = findViewById(R.id.btnDangNhapFacebook);
        mDangNhapFacebook.setReadPermissions("email", "public_profile");
        mDangNhapFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                String tokenID = loginResult.getAccessToken().getToken();
                AuthCredential authCredential = FacebookAuthProvider.getCredential(tokenID);
                mLoginFacebookProgress.setTitle("Đăng nhập Bằng Tài Khoản Facebook");
                mLoginFacebookProgress.setMessage("Chúng tôi đang kiểm tra thông tin cho tài khoản của bạn. Vui lòng đợi trong giây lát");
                mLoginFacebookProgress.setCanceledOnTouchOutside(false);
                mLoginFacebookProgress.show();
                mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser facebookUser = mAuth.getCurrentUser();
                            userID = facebookUser.getUid();
                            username = (!facebookUser.getDisplayName().toString().equals("")) ? facebookUser.getDisplayName().toString() : "";

                            anhdaidien = (!facebookUser.getPhotoUrl().equals("")) ? facebookUser.getPhotoUrl().toString() : "https://firebasestorage.googleapis.com/v0/b/apphenhosinhvien.appspot.com/o/anhdaidien%2Fimg_add_default.png?alt=media&token=7b82a176-a34b-442b-8e26-85f5dc6dfb54";

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                            userMap = new HashMap<>();
                            mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null){
                                        gioitinh = dataSnapshot.child("gioitinh").getValue().toString();
                                        ngaysinh = dataSnapshot.child("ngaysinh").getValue().toString();
                                        truong = dataSnapshot.child("truong").getValue().toString();
                                        sothich = dataSnapshot.child("sothich").getValue().toString();
                                        userMap.put("gioitinh",gioitinh);
                                        userMap.put("ngaysinh",ngaysinh);
                                        userMap.put("truong",truong);
                                        userMap.put("sothich",sothich);
                                        userMap.put("username", username);
                                        userMap.put("anhdaidien", anhdaidien);
                                        userMap.put("ID", userID);

                                    }else{
                                        userMap.put("gioitinh","");
                                        userMap.put("ngaysinh","");
                                        userMap.put("truong","");
                                        userMap.put("sothich","");
                                        userMap.put("username", username);
                                        userMap.put("anhdaidien", anhdaidien);
                                        userMap.put("ID", userID);

                                    }

                                    mUserDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mLoginFacebookProgress.dismiss();
                                                Toast.makeText(DangNhapActivity.this, "Đăng nhập bằng tài khoản Facebook thành công", Toast.LENGTH_SHORT).show();
                                                if(username.equals("") || gioitinh.equals("") || ngaysinh.equals("") || truong.equals("") || sothich.equals("")) {
                                                    Intent themThongTinIntent = new Intent(DangNhapActivity.this, CheckThongTinDangNhapActivity.class);
                                                    themThongTinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(themThongTinIntent);
                                                    finish();
                                                }else{
                                                    Intent mainIntent = new Intent(DangNhapActivity.this, MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            }else{
                                                mLoginFacebookProgress.hide();
                                                Toast.makeText(DangNhapActivity.this, "Đăng nhập bằng tài khoản Facebook thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }else{
                            mLoginFacebookProgress.hide();
                            Toast.makeText(DangNhapActivity.this, "Đăng nhập bằng tài khoản Facebook thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        Intent intent = getIntent();
        String email = intent.getStringExtra("Email");
        String matKhau = intent.getStringExtra("Mat Khau");
        if (email != null && matKhau != null){
            edtTenDangNhap.setText(email);
            edtMatKhau.setText(matKhau);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Đăng nhập với google
        if(requestCode == 3){
            if(resultCode == RESULT_OK){
                final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                final String tokenID = result.getSignInAccount().getIdToken();
                AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenID,null);
                mLoginGoogleProgress.setTitle("Đăng nhập Bằng Tài Khoản Google");
                mLoginGoogleProgress.setMessage("Chúng tôi đang kiểm tra thông tin cho tài khoản của bạn. Vui lòng đợi trong giây lát");
                mLoginGoogleProgress.setCanceledOnTouchOutside(false);
                mLoginGoogleProgress.show();
                mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser googleUser = mAuth.getCurrentUser();
                            userID = googleUser.getUid();
                            username = (!googleUser.getDisplayName().toString().equals("")) ? googleUser.getDisplayName().toString() : "";
                            anhdaidien = (!result.getSignInAccount().getPhotoUrl().toString().equals("")) ? result.getSignInAccount().getPhotoUrl().toString() : "https://firebasestorage.googleapis.com/v0/b/apphenhosinhvien.appspot.com/o/anhdaidien%2Fimg_add_default.png?alt=media&token=7b82a176-a34b-442b-8e26-85f5dc6dfb54";
                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                            userMap = new HashMap<>();
                            mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue() != null){
                                        gioitinh = dataSnapshot.child("gioitinh").getValue().toString();
                                        ngaysinh = dataSnapshot.child("ngaysinh").getValue().toString();
                                        truong = dataSnapshot.child("truong").getValue().toString();
                                        sothich = dataSnapshot.child("sothich").getValue().toString();
                                        userMap.put("gioitinh",gioitinh);
                                        userMap.put("ngaysinh",ngaysinh);
                                        userMap.put("truong",truong);
                                        userMap.put("sothich",sothich);
                                        userMap.put("username", username);
                                        userMap.put("anhdaidien", anhdaidien);
                                        userMap.put("ID", userID);

                                    }else{
                                        userMap.put("gioitinh","");
                                        userMap.put("ngaysinh","");
                                        userMap.put("truong","");
                                        userMap.put("sothich","");
                                        userMap.put("username", username);
                                        userMap.put("anhdaidien", anhdaidien);
                                        userMap.put("ID", userID);

                                    }

                                    mUserDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mLoginProgress.dismiss();
                                                Toast.makeText(DangNhapActivity.this, "Đăng nhập bằng tài khoản google thành công", Toast.LENGTH_SHORT).show();
                                                if(username.equals("") || gioitinh.equals("") || ngaysinh.equals("") || truong.equals("") || sothich.equals("")) {
                                                    Intent themThongTinIntent = new Intent(DangNhapActivity.this, CheckThongTinDangNhapActivity.class);
                                                    themThongTinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(themThongTinIntent);
                                                    finish();
                                                }else{
                                                    Intent mainIntent = new Intent(DangNhapActivity.this, MainActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            }
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                        }else{
                            mLoginGoogleProgress.hide();
                            Toast.makeText(DangNhapActivity.this, "Đăng nhập bằng tài khoản google thất bại", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().logOut();
    }

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "Nhấn thêm lần nữa để thoát!", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }
}

