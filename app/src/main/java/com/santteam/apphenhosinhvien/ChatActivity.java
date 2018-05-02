package com.santteam.apphenhosinhvien;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {
    private Toolbar mToolbarChat;
    private ImageView mAdd, mSendChat;
    private EmojiconEditText mNoiDungChat;
    private String mUserID, mNameUser,mNameMe;
    private TextView mUsername, mLastSeen;
    private CircleImageView mAnhDaiDien;
    private RecyclerView mMessagesList;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mCurrentStateChatsDatabase;
    private DatabaseReference mLikesDatabase;
    private DatabaseReference mRequestsLikeDatabase;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private ArrayList<Messages> messagesList = new ArrayList<>();
    private MessagesAdapter mMessagesAdapter;

    private ProgressDialog mImageProgress;
    private LinearLayout mlnChucNang, mlnXacNhan;
    private Button mXacNhan, mTuChoi;


    private int GALLERY_PICK = 1;

    private int mCheck_BanBe = 0;
    private int mCheck_XacNhan = 0;

    private int mCheck_IconChat = 0;
    private int mCheck_KetNoi = 0;
    private String mCurrentFriend;
    private String mCurrentXacNhan;
    private String mCurrentKetNoi;
    private String mCurrentKiemTraKetNoi;

    private ImageView mIconChat;

    private LinearLayout mlnKetNoi;
    private Button mXacNhanKetNoi;
    private Button mTuChoiKetNoi;
    private TextView mThongBaoKetNoi;
    private ImageView mUserLike;
    private EmojIconActions emojIcon;
    private int check = 0;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        if (intent != null) {
            mUserID = intent.getStringExtra("ID");
        }

        mCurrentFriend = "not_friend";
        mCurrentXacNhan = "not_ok";
        mCurrentKetNoi = "not_like";
        mCurrentKiemTraKetNoi = "not_connect";
        addControls();

        addFirebase();
        addEvent();

        if (mCurrentUser != null) {
            setSupportActionBar(mToolbarChat);
            final ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View actionBarView = inflater.inflate(R.layout.chat_custom_bar, null);

            actionBar.setCustomView(actionBarView);

            //--------Custom action bar
            mUsername = (TextView) findViewById(R.id.tvUsername);
            mLastSeen = (TextView) findViewById(R.id.tvLastSeen);
            mAnhDaiDien = (CircleImageView) findViewById(R.id.imgAnhDaiDien);
            mUserLike = findViewById(R.id.imgLike);
            mMessagesAdapter = new MessagesAdapter(ChatActivity.this, messagesList);
            mMessagesList.setAdapter(mMessagesAdapter);

            emojIcon = new EmojIconActions(ChatActivity.this, mMessagesList, mNoiDungChat, mIconChat);

            emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
                @Override
                public void onKeyboardOpen() {
                    Log.e("Keyboard", "open");
                }

                @Override
                public void onKeyboardClose() {
                    Log.e("Keyboard", "close");
                }
            });


            emojIcon.setUseSystemEmoji(true);

            mRootRef.child("users").child(mUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mNameUser = dataSnapshot.child("username").getValue().toString();
                    String linkImage = dataSnapshot.child("anhdaidien").getValue().toString();
                    String online = dataSnapshot.child("online").getValue().toString();
                    if (online.equals("true")) {
                        mLastSeen.setText("Online");
                    } else {
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long lastTime = Long.parseLong(online);
                        String lastSeenTime = (String) DateUtils.getRelativeDateTimeString(getApplicationContext(), lastTime, DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_NUMERIC_DATE);
                        mLastSeen.setText(lastSeenTime);
                    }

                    mUsername.setText(mNameUser);
                    Picasso.with(getBaseContext()).load(linkImage).placeholder(R.drawable.img_add_default).into(mAnhDaiDien);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mRootRef.child("users").child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mNameMe = dataSnapshot.child("username").getValue().toString();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            mRootRef.child("chats").child(mCurrentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(mUserID)) {


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

            mCurrentStateChatsDatabase.child(mCurrentUser.getUid()+'/'+mUserID+"/request_chat_type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object value = dataSnapshot.getValue();
                    if(value != null) {
                        if ("received".equals(value.toString())) {
                            mlnXacNhan.setVisibility(View.VISIBLE);
                            //mlnChucNang.setVisibility(View.GONE);
                        }
                    }

                    mCurrentStateChatsDatabase.child(mCurrentUser.getUid()+'/'+mUserID+"/request_chat_type").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if("ok".equals(dataSnapshot.getValue())) {
                                mCurrentXacNhan = "ok";

                            }
                            if(mCheck_XacNhan == 0) {
                                loadMessage();
                            }
                            mCheck_XacNhan = 1;
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


            mFriendsDatabase.child(mCurrentUser.getUid()+'/'+mUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object value = dataSnapshot.getValue();
                    if(value != null) {

                        mCurrentFriend = "friend";
                        mRootRef.child("current_state_chats").child(mCurrentUser.getUid()).child(mUserID).child("request_chat_type").setValue("ok").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mRootRef.child("current_state_chats").child(mUserID).child(mCurrentUser.getUid()).child("request_chat_type").setValue("ok");
                            }
                        });

                        /*
                        //chuyen chat_request sang chat
                        mRootRef.child("chats").child(mUserID).child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        */
                        //chuyen message_request sang message
                        mRootRef.child("messages").child(mUserID).child(mCurrentUser.getUid()).addChildEventListener(new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                if(dataSnapshot.getValue() != null) {
                                    String push_id = dataSnapshot.getKey();
                                    String from = dataSnapshot.child("from").getValue().toString();
                                    String message = dataSnapshot.child("message").getValue().toString();
                                    Long time = Long.parseLong(dataSnapshot.child("time").getValue().toString());
                                    String type = dataSnapshot.child("type").getValue().toString();

                                    String current_user_ref = "messages/" + mCurrentUser.getUid() + "/" + mUserID;

                                    Map messageMap = new HashMap();
                                    messageMap.put("message", message);
                                    messageMap.put("seen", false);
                                    messageMap.put("type", type);
                                    messageMap.put("time", time);
                                    messageMap.put("from", from);
                                    //updateChats(message);
                                    Map messageUserMap = new HashMap();
                                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);


                                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                            }
                                        }
                                    });

                                    mRootRef.child("message_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mRootRef.child("message_requests").child(mUserID).child(mCurrentUser.getUid()).removeValue();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mRequestsLikeDatabase.child(mCurrentUser.getUid()+'/'+mUserID + "/type").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object value = dataSnapshot.getValue();
                    if(value != null) {
                        if ("received".equals(value.toString())) {
                            mThongBaoKetNoi.setText("Bạn vừa nhận được yêu cầu kết nối từ " + mNameUser);
                            mlnKetNoi.setVisibility(View.VISIBLE);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mLikesDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object value = dataSnapshot.getValue();
                    if(value != null) {
                        mCurrentKiemTraKetNoi = "connect";
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            mLikesDatabase.child(mCurrentUser.getUid()+'/'+mUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object value = dataSnapshot.getValue();
                    if(value != null) {
                        mCurrentKetNoi = "like";
                        mMessagesList.setBackgroundResource(R.drawable.img_bg_chat_love);
                        mUserLike.setVisibility(View.VISIBLE);
                    }else{
                        mUserLike.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            mSendChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessage();
                    mNoiDungChat.setText("");

                }
            });

            mImageStorage = FirebaseStorage.getInstance().getReference();
            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                }
            });


            mTuChoi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mRootRef.child("chat_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue();

                    mRootRef.child("message_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue();
                    finish();

                }
            });

            mXacNhan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //chuyen chat_request sang chat
                    mRootRef.child("chats").child(mUserID).child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
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

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    //chuyen chat request_message sang message
                    mRootRef.child("messages").child(mUserID).child(mCurrentUser.getUid()).addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String push_id = dataSnapshot.getKey();
                            String from = dataSnapshot.child("from").getValue().toString();
                            String message = dataSnapshot.child("message").getValue().toString();
                            Long time = Long.parseLong(dataSnapshot.child("time").getValue().toString());
                            String type = dataSnapshot.child("type").getValue().toString();

                            String current_user_ref = "messages/" + mCurrentUser.getUid() + "/" + mUserID;

                            Map messageMap = new HashMap();
                            messageMap.put("message", message);
                            messageMap.put("seen", false);
                            messageMap.put("type", type);
                            messageMap.put("time", time);
                            messageMap.put("from", from);
                            updateChats(message);
                            Map messageUserMap = new HashMap();
                            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);


                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                    }
                                }
                            });

                            mRootRef.child("message_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mRootRef.child("message_requests").child(mUserID).child(mCurrentUser.getUid()).removeValue();
                                }
                            });

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mRootRef.child("current_state_chats").child(mCurrentUser.getUid()).child(mUserID).child("request_chat_type").setValue("ok").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mRootRef.child("current_state_chats").child(mUserID).child(mCurrentUser.getUid()).child("request_chat_type").setValue("ok").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mlnChucNang.setVisibility(View.VISIBLE);
                                    mlnXacNhan.setVisibility(View.GONE);
                                }
                            });
                        }
                    });

                    mCheck_XacNhan = 0;

                }
            });

            mXacNhanKetNoi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    if(!mCurrentKiemTraKetNoi.equals("connect")) {
                        mRootRef.child("likes").child(mCurrentUser.getUid()).child(mUserID).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mRootRef.child("likes").child(mUserID).child(mCurrentUser.getUid()).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mCurrentKetNoi = "like";
                                        mCurrentKiemTraKetNoi = "connect";
                                        mMessagesList.setBackgroundResource(R.drawable.img_bg_chat_love);
                                        mlnKetNoi.setVisibility(View.GONE);
                                        mRootRef.child("like_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mRootRef.child("like_requests").child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ChatActivity.this, "Bạn và " + mNameUser + " đã kết nối với nhau.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });
                            }
                        });
                    }else{
                        Toast.makeText(ChatActivity.this, "Bạn đã kết nối với một người khác rồi!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mTuChoiKetNoi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRootRef.child("like_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mRootRef.child("like_requests").child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mCurrentKetNoi = "not_like";
                                    mCurrentKiemTraKetNoi = "not_connect";
                                    mlnKetNoi.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
        }
    }


    private void addFirebase() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mCurrentStateChatsDatabase = FirebaseDatabase.getInstance().getReference().child("current_state_chats");
        mLikesDatabase = FirebaseDatabase.getInstance().getReference().child("likes");
        mRequestsLikeDatabase = FirebaseDatabase.getInstance().getReference().child("like_requests");

    }

    private void updateChats(String s) {
        if (mCurrentFriend.equals("friend") || mCurrentXacNhan.equals("ok")) {
            Map chatAddMap = new HashMap();
            chatAddMap.put("seen", false);
            chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
            chatAddMap.put("lastmessage", s.equals("") ? "Không có tin nhắn nào" : s);
            Map chatUserMap = new HashMap();
            chatUserMap.put("chats/" + mCurrentUser.getUid() + "/" + mUserID, chatAddMap);
            chatUserMap.put("chats/" + "/" + mUserID + "/" + mCurrentUser.getUid(), chatAddMap);

            mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }

                }
            });
        } else {
            Map chatAddMap = new HashMap();
            chatAddMap.put("seen", false);
            chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
            chatAddMap.put("lastmessage", s.equals("") ? "Không có tin nhắn nào" : s);
            Map chatUserMap = new HashMap();
            //chatUserMap.put("chat_requests/" + mCurrentUser.getUid() + "/" + mUserID, chatAddMap);
            chatUserMap.put("chat_requests/" + "/" + mUserID + "/" + mCurrentUser.getUid(), chatAddMap);

            chatUserMap.put("chats/" + mCurrentUser.getUid() + "/" + mUserID, chatAddMap);
            mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }

                }
            });
        }
    }


    private void loadMessage() {

        if (mCurrentFriend.equals("friend") || mCurrentXacNhan.equals("ok")) {

            mRootRef.child("messages").child(mCurrentUser.getUid()).child(mUserID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages mess = dataSnapshot.getValue(Messages.class);
                    mess.setFrom(dataSnapshot.child("from").getValue().toString());
                    messagesList.add(mess);
                    mMessagesAdapter.notifyDataSetChanged();

                    mMessagesList.scrollToPosition(messagesList.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            mRootRef.child("message_requests").child(mCurrentUser.getUid()).child(mUserID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages mess = dataSnapshot.getValue(Messages.class);
                    mess.setFrom(dataSnapshot.child("from").getValue().toString());
                    messagesList.add(mess);
                    mMessagesAdapter.notifyDataSetChanged();
                    mMessagesList.scrollToPosition(messagesList.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void sendMessage() {
        String message = mNoiDungChat.getText().toString().trim();
        if (!message.equals("")) {
            if (mCurrentFriend.equals("friend") || mCurrentXacNhan.equals("ok")) {

                //dua tin nhan vao hop thu chat
                String current_user_ref = "messages/" + mCurrentUser.getUid() + "/" + mUserID;
                String chat_user_ref = "messages/" + mUserID + "/" + mCurrentUser.getUid();

                DatabaseReference user_message_push = mRootRef.child("messages")
                        .child(mCurrentUser.getUid()).child(mUserID).push();

                String push_id = user_message_push.getKey();

                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", mCurrentUser.getUid());
                updateChats(message);
                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
                        }
                    }
                });
            } else {
                //dua tin nhan vao hop thu request chat
                String current_user_ref_req = "message_requests/" + mCurrentUser.getUid() + "/" + mUserID;
                String chat_user_ref_req = "message_requests/" + mUserID + "/" + mCurrentUser.getUid();

                String current_user_ref = "messages/" + mCurrentUser.getUid() + "/" + mUserID;

                DatabaseReference user_message_request_push = mRootRef.child("message_requests")
                        .child(mCurrentUser.getUid()).child(mUserID).push();

                String push_id = user_message_request_push.getKey();

                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", mCurrentUser.getUid());
                updateChats(message);
                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref_req + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref_req + "/" + push_id, messageMap);

                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);

                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
                        }
                    }
                });
                mCurrentStateChatsDatabase.child(mCurrentUser.getUid()).child(mUserID).child("request_chat_type")
                        .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mCurrentStateChatsDatabase.child(mUserID).child(mCurrentUser.getUid()).child("request_chat_type")
                                .setValue("received");
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            mImageProgress = new ProgressDialog(this);
            mImageProgress.setTitle("Sending Image");
            mImageProgress.setMessage("Chúng tôi đang gửi ảnh cho bạn. Vui lòng đợi trong giây lát");
            mImageProgress.setCanceledOnTouchOutside(false);
            mImageProgress.show();
            Uri imageUri = data.getData();
            if (mCurrentUser != null) {
                if (mCurrentFriend.equals("friend") || mCurrentXacNhan.equals("ok")) {

                    final String current_user_ref = "messages/" + mCurrentUser.getUid() + "/" + mUserID;
                    final String chat_user_ref = "messages/" + mUserID + "/" + mCurrentUser.getUid();

                    DatabaseReference user_message_push = mRootRef.child("messages")
                            .child(mCurrentUser.getUid()).child(mUserID).push();

                    final String push_id = user_message_push.getKey();

                    StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                String download_uri = task.getResult().getDownloadUrl().toString();

                                Map messageMap = new HashMap();
                                messageMap.put("message", download_uri);
                                messageMap.put("seen", false);
                                messageMap.put("type", "image");
                                messageMap.put("time", ServerValue.TIMESTAMP);
                                messageMap.put("from", mCurrentUser.getUid());
                                updateChats(download_uri);
                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                        }
                                    }
                                });
                                mImageProgress.dismiss();

                            } else {
                                mImageProgress.hide();
                                Toast.makeText(ChatActivity.this, "Gửi ảnh không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    final String current_user_ref_req = "message_requests/" + mCurrentUser.getUid() + "/" + mUserID;
                    final String chat_user_ref_req = "message_requests/" + mUserID + "/" + mCurrentUser.getUid();
                    final String chat_user_ref = "messages/" + mCurrentUser.getUid() + "/" + mUserID;

                    DatabaseReference user_message_push = mRootRef.child("message_requests")
                            .child(mCurrentUser.getUid()).child(mUserID).push();

                    final String push_id = user_message_push.getKey();

                    StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                String download_uri = task.getResult().getDownloadUrl().toString();

                                Map messageMap = new HashMap();
                                messageMap.put("message", download_uri);
                                messageMap.put("seen", false);
                                messageMap.put("type", "image");
                                messageMap.put("time", ServerValue.TIMESTAMP);
                                messageMap.put("from", mCurrentUser.getUid());
                                updateChats(download_uri);
                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref_req + "/" + push_id, messageMap);
                                messageUserMap.put(chat_user_ref_req + "/" + push_id, messageMap);
                                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                        }
                                    }
                                });
                                mCurrentStateChatsDatabase.child(mCurrentUser.getUid()).child(mUserID).child("request_chat_type")
                                        .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mCurrentStateChatsDatabase.child(mUserID).child(mCurrentUser.getUid()).child("request_chat_type")
                                                .setValue("received");
                                    }
                                });
                                mImageProgress.dismiss();

                            } else {
                                mImageProgress.hide();
                                Toast.makeText(ChatActivity.this, "Gửi ảnh không thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            CountDownTimer countDownTimer = new CountDownTimer(50,25) {
                @Override
                public void onTick(long l) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mNoiDungChat.getWindowToken(), 0);
                }

                @Override
                public void onFinish() {
                    finish();
                }
            };
            countDownTimer.start();

        }
        if(item.getItemId() == R.id.iconMenuChat){
            mDrawerLayout.openDrawer(GravityCompat.END);
        }
        return super.onOptionsItemSelected(item);
    }


    private void addEvent() {

        mIconChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojIcon.ShowEmojIcon();

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

        NavigationView mNavi = (NavigationView) findViewById(R.id.navigationChat);
        mNavi.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mnuThongtinChat:
                    {
                        Intent trangCaNhanIntent = new Intent(ChatActivity.this, TrangCaNhanActivity.class);
                        trangCaNhanIntent.putExtra("ID", mUserID);
                        startActivity(trangCaNhanIntent);
                        break;
                    }
                    case R.id.mnuXoaChat:
                    {

                        if(mCurrentFriend.equals("friend") || mCurrentXacNhan.equals("ok")) {
                            mRootRef.child("chats").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mRootRef.child("messages").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent listChatIntent = new Intent(ChatActivity.this, ListActivity.class);
                                            startActivity(listChatIntent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }else {
                            mRootRef.child("chat_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mRootRef.child("message_requests").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent listChatIntent = new Intent(ChatActivity.this, ListActivity.class);
                                            startActivity(listChatIntent);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }

                        break;
                    }
                    case R.id.mnuYeuthichChat:
                    {
                        if(mCurrentFriend.equals("friend")){
                            if (mCurrentKetNoi.equals("not_like")) {

                                if(mCurrentKiemTraKetNoi.equals("connect")){
                                    Toast.makeText(ChatActivity.this, "Bạn đã kết nối với một người khác rồi!", Toast.LENGTH_SHORT).show();
                                }else {
                                    mRootRef.child("like_requests").child(mCurrentUser.getUid()).child(mUserID).child("type").setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mRootRef.child("like_requests").child(mUserID).child(mCurrentUser.getUid()).child("type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ChatActivity.this, "Gửi yêu cầu kết nối thành công!\nVui lòng chờ " + mNameUser + " xác nhận.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            } else {
                                mRootRef.child("likes").child(mCurrentUser.getUid()).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mRootRef.child("likes").child(mUserID).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ChatActivity.this, "Bạn vừa hủy kết nối với  " + mNameUser + ".", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                            break;
                        }else{

                            Toast.makeText(ChatActivity.this, "Bạn và " + mNameUser + " chưa kết bạn với nhau!", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
                return true;
            }
        });
    }
    private void addControls() {

        mToolbarChat = findViewById(R.id.toolBarChat);
        mAdd = findViewById(R.id.imgAdd);
        mSendChat = findViewById(R.id.imgSend);
        mNoiDungChat = findViewById(R.id.edtNoiDung);
        mMessagesList = findViewById(R.id.rvListChatMessage);
        mlnChucNang = findViewById(R.id.lnChucNang);
        mlnXacNhan = findViewById(R.id.lnXacNhan);
        mXacNhan = findViewById(R.id.btnXacNhan);
        mTuChoi = findViewById(R.id.btnTuChoi);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(new LinearLayoutManager(this));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbarChat = findViewById(R.id.toolBarChat);

        mIconChat = findViewById(R.id.imgIconChat);
        mlnKetNoi = findViewById(R.id.lnKetNoi);
        mXacNhanKetNoi = findViewById(R.id.btnXacNhanKetNoi);
        mTuChoiKetNoi = findViewById(R.id.btnTuChoiKetNoi);
        mThongBaoKetNoi = findViewById(R.id.tvKetNoi);

    }

    @Override
    public void onBackPressed() {

        if(check == 0) {
            super.onBackPressed();
            finish();
        }else {
            mDrawerLayout.closeDrawer(Gravity.END);
            check = 1;
        }
    }

}
