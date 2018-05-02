package com.santteam.apphenhosinhvien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santteam.apphenhosinhvien.model.ImageFullSCActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;

public class TrangCaNhanActivity extends AppCompatActivity{
    private static final String TAG = TrangCaNhanActivity.class.getName();
    private TextView tvUsername,tvGioiTinh,tvDiaChi,tvTruong,tvNgaySinh,tvSoThich;
    private ImageView imgBack,imgAnhDaiDien,mUserLike;
    private TableRow mTableRowKetBan,mTableRowXacNhan;
    private Button btnKetBan,btnNhanTin,btnDongY,btnTuChoi;
    PhotoViewAttacher mAttacher;
    int PICK_IMAGE=1;
    int CAMERA_IMAGE=2;
    //User
    private FirebaseUser mCurrentUser;
    private String mUserID;

    //Database
    private DatabaseReference databaseReference;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mOnlineDatabase;
    private DatabaseReference mLikesDatabaseReference;
    private DatabaseReference mRootRef;
    //storage
    private StorageReference mImageStorageReference;

    private ProgressDialog mProgressDialog;

    private String linkanhdaidien;

    private Intent intent;

    String mCurrentState = "not_friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_ca_nhan);
        addcontrols();
        addFirebase();
        addEvents();
    }

    private void addFirebase() {
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        if(intent != null){
            mUserID = intent.getStringExtra("ID");
        }
        if(mUserID == null) {
            mUserID = mCurrentUser.getUid();
        } else{
            mTableRowKetBan.setVisibility(View.VISIBLE);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUserID);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");

        mImageStorageReference = FirebaseStorage.getInstance().getReference();
        mLikesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("likes");
        mRootRef = FirebaseDatabase.getInstance().getReference();



    }

    private void addEvents() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //--------------------Popup Ảnh đại diện---------
        imgAnhDaiDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(TrangCaNhanActivity.this, v);
                if(mUserID.equals(mCurrentUser.getUid())) {
                    popup.inflate(R.menu.popupmenu_avarta);
                }
                else  popup.inflate(R.menu.popupmenu_avatar_khach);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(mUserID.equals(mCurrentUser.getUid())){
                            switch (item.getItemId()){
                                case R.id.menuXemAnh:{
                                    viewPicture();
                                    return true;
                                }
                                case R.id.menuChupAnh: {
                                    cameraPicture();
                                    return true;
                                }
                                case R.id.menuThuVien:{
                                    choosePictureinLibrary();
                                    return true;
                                }
                            }
                        }else{
                            switch (item.getItemId()) {
                                case R.id.menuXemAnh: {
                                    viewPicture();
                                    return true;
                                }
                            }
                        }

                        return true;
                    }
                });
                popup.show();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linkanhdaidien = dataSnapshot.child("anhdaidien").getValue().toString();
                Picasso.with(TrangCaNhanActivity.this).load(dataSnapshot.child("anhdaidien").getValue().toString()).placeholder(R.drawable.img_add_default).into(imgAnhDaiDien);
                tvUsername.setText(dataSnapshot.child("username").getValue().toString());
                tvGioiTinh.setText(dataSnapshot.child("gioitinh").getValue().toString());
                tvTruong.setText(dataSnapshot.child("truong").getValue().toString());
                tvNgaySinh.setText(dataSnapshot.child("ngaysinh").getValue().toString());
                tvSoThich.setText(dataSnapshot.child("sothich").getValue().toString().trim());

                mLikesDatabaseReference.child(mCurrentUser.getUid()).child(mUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null) {
                            mUserLike.setVisibility(View.VISIBLE);
                        }
                        else {
                            mUserLike.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //========================Xử lý lời mời===============
                mFriendRequestDatabase.child(mCurrentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //================= Kiểm tra lời mời==============
                                if(dataSnapshot.hasChild(mUserID)){
                                    String req_type = dataSnapshot.child(mUserID)
                                            .child("request_type").getValue().toString();
                                    if(req_type.equals("received")){
                                        mCurrentState = "req_received";
                                        btnKetBan.setText("Xác nhận");
                                        mTableRowXacNhan.setVisibility(View.VISIBLE);
                                    } else if(req_type.equals("sent")){
                                        mCurrentState = "req_sent";
                                        btnKetBan.setText("Hủy lời mời");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                //==========================Xử lý kết bạn===========
                mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(mUserID)){

                            mCurrentState = "friends";
                            btnKetBan.setText("Hủy kết bạn");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnKetBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //====================== Gửi lời mời kết bạn=======================
                if(mCurrentState.equals("not_friends")){
                   mFriendRequestDatabase.child(mCurrentUser.getUid())
                           .child(mUserID).child("request_type")
                           .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               mFriendRequestDatabase.child(mUserID).child(mCurrentUser.getUid())
                                       .child("request_type")
                                       .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       mCurrentState = "req_sent";
                                       btnKetBan.setText("Hủy lời mời");
                                   }
                               });
                           } else{
                               Toast.makeText(TrangCaNhanActivity.this, "Gửi lời mời kết bạn thất bại!", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
                }
                //======================Hủy lời mời kết bạn"=================
                if(mCurrentState.equals("req_sent")){
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendRequestDatabase.child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mCurrentState = "not_friends";
                                    btnKetBan.setText("Kết bạn");
                                }
                            });

                        }
                    });

                }
                //=======================Hủy kết bạn================
                if(mCurrentState.equals("friends")){

                    mFriendDatabase.child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(TrangCaNhanActivity.this, "Hủy kết bạn thành công!", Toast.LENGTH_SHORT).show();
                                    mCurrentState = "not_friends";
                                    btnKetBan.setText("Kết bạn");
                                }
                            });
                        }
                    });
                    mLikesDatabaseReference.child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mLikesDatabaseReference.child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(TrangCaNhanActivity.this, "Bạn vừa hủy kết nối!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //=================Xác nhận lời mời======================
                if(mCurrentState.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendDatabase.child(mCurrentUser.getUid()).child(mUserID)
                            .child("date")
                            .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(mUserID).child(mCurrentUser.getUid())
                                    .child("date")
                                    .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mFriendRequestDatabase.child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mCurrentState = "friends";
                                                    btnKetBan.setText("Hủy kết bạn");
                                                    mTableRowXacNhan.setVisibility(View.GONE);
                                                }
                                            });

                                        }
                                    });
                                    mRootRef.child("chats").child(mUserID).child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getValue() != null) {
                                                String lastmessage = dataSnapshot.child("lastmessage").getValue().toString();
                                                long timestamp = Long.parseLong(dataSnapshot.child("timestamp").getValue().toString());

                                                Map chatAddMap = new HashMap();
                                                chatAddMap.put("seen", false);
                                                chatAddMap.put("timestamp", timestamp);
                                                chatAddMap.put("lastmessage", lastmessage.equals("") ? "Không có tin nhắn nào" : lastmessage);
                                                Map chatUserMap = new HashMap();
                                                chatUserMap.put("chats/" + mCurrentUser.getUid() + "/" + mUserID, chatAddMap);

                                                mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                        if (databaseError != null) {
                                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                                        }

                                                    }
                                                });

                                                mRootRef.child("chat_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mRootRef.child("chat_requests").child(mUserID).child(mCurrentUser.getUid()).removeValue();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }
                            });

                        }
                    });


                }
            }
        });

        btnTuChoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //==================Từ chối lời mời===================
                mFriendRequestDatabase.child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendRequestDatabase.child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mCurrentState = "not_friends";
                                btnKetBan.setText("Kết bạn");
                                mTableRowXacNhan.setVisibility(View.GONE);
                            }
                        });

                    }
                });
            }
        });

        btnNhanTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(TrangCaNhanActivity.this,ChatActivity.class);
                chatIntent.putExtra("ID",mUserID);
                startActivity(chatIntent);
                finish();
            }
        });



    }

    private void choosePictureinLibrary() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), PICK_IMAGE);
    }
    private void viewPicture() {
        Intent intent = new Intent(TrangCaNhanActivity.this, ImageFullSCActivity.class );
        intent.putExtra("linkanhdaidien", linkanhdaidien);
        startActivity(intent);
    }

    private void cameraPicture() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_IMAGE);
        } else {
            Toast.makeText(getApplication(), "Camera không được hỗ trợ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Nhận kết quả từ Camera trả về và đưa lên database
        if (resultCode == RESULT_OK ) {
            mProgressDialog = new ProgressDialog(TrangCaNhanActivity.this);
            mProgressDialog.setTitle("Tải lên ảnh đại diện");
            mProgressDialog.setMessage("Chúng tôi đang tải lên ảnh đại diện mới cho bạn!");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            if (requestCode == CAMERA_IMAGE && data != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://apphenhosinhvien.appspot.com/");
                StorageReference mountainImagesRef = storageRef.child("anhdaidien/"+"anhdaidien_" + mCurrentUser.getUid() + ".jpg");

                Bitmap bitmap1 = (Bitmap) data.getExtras().get("data");//mặc định
                imgAnhDaiDien.setImageBitmap(bitmap1);

                imgAnhDaiDien.setDrawingCacheEnabled(true);
                imgAnhDaiDien.buildDrawingCache();
                Bitmap bitmap = imgAnhDaiDien.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataBytes = baos.toByteArray();

                UploadTask uploadTask = mountainImagesRef.putBytes(dataBytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        mProgressDialog.hide();
                        Toast.makeText(TrangCaNhanActivity.this, "Cập nhật thất bại, hãy thử lại !", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                        databaseReference.child("anhdaidien").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(TrangCaNhanActivity.this, "Tải lên thành công!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
            //Nhận kết quả từ Library Picture trả về và đưa lên database
            if (requestCode == PICK_IMAGE) {
                Uri imageUri = data.getData();

                StorageReference filepath = mImageStorageReference.child("anhdaidien").child("anhdaidien_" + mCurrentUser.getUid() + ".jpg");
                filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String download_url = task.getResult().getDownloadUrl().toString();
                            databaseReference.child("anhdaidien").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(TrangCaNhanActivity.this, "Tải lên thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            mProgressDialog.hide();
                            Toast.makeText(TrangCaNhanActivity.this, "Đã xãy ra lỗi khi tải lên!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void addcontrols() {
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        imgAnhDaiDien = (ImageView) findViewById(R.id.imgAnhDaiDien);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        tvGioiTinh = (TextView) findViewById(R.id.tvGioiTinh);
        tvDiaChi = (TextView) findViewById(R.id.tvDiaChi);
        tvNgaySinh = (TextView) findViewById(R.id.tvNgaySinh);
        tvTruong = (TextView) findViewById(R.id.tvTruong);
        tvSoThich = (TextView) findViewById(R.id.tvSoThich);
        mTableRowKetBan = findViewById(R.id.tableRowKetBan);
        mTableRowXacNhan = findViewById(R.id.tableRowXacNhan);
        btnKetBan = findViewById(R.id.btnKetBan);
        btnNhanTin = findViewById(R.id.btnNhanTin);
        btnDongY = findViewById(R.id.btnDongY);
        btnTuChoi = findViewById(R.id.btnTuChoi);
        mUserLike = findViewById(R.id.imgLike);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
