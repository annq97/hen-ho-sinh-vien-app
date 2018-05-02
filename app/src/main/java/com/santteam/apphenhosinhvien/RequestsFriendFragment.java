package com.santteam.apphenhosinhvien;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsFriendFragment extends Fragment {

    private RecyclerView mRequestsFriendList;

    private DatabaseReference mRequestsFriendDatabase;
    private DatabaseReference mRequestsFriendData;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mUserID;

    private View mMainView;

    private ArrayList<RequestsFriend> mRequestsFriends;

    private RecyclerAdapterRequestFriend mRequestFriendAdapter;

    private Listener mListener;

    public RequestsFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_requests_friend, container, false);
        addControls();
        mRequestsFriends = new ArrayList<>();
        mUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRequestsFriendData = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        mListener = new Listener();
        mRequestsFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests").child(mUserID);
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mRequestsFriendDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mRequestsFriendDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("request_type").getValue().toString().equals("received")){
                    RequestsFriend requestsFriend = dataSnapshot.getValue(RequestsFriend.class);
                    requestsFriend.setID(dataSnapshot.getKey().toString());
                    mRequestsFriends.add(requestsFriend);
                    mRequestFriendAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(getContext(), "Bạn vừa thay đổi gì ư", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(RequestsFriend rf : mRequestsFriends){
                    if(rf.getID().equals(dataSnapshot.getKey())){
                        mRequestsFriends.remove(rf);
                        mRequestFriendAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mRequestsFriendList.setHasFixedSize(true);
        mRequestsFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        mRequestFriendAdapter = new RecyclerAdapterRequestFriend(mRequestsFriends,getActivity(),mListener);
        mRequestsFriendList.setAdapter(mRequestFriendAdapter);



        return mMainView;
    }


    private void addControls() {
        mRequestsFriendList = mMainView.findViewById(R.id.rvRequestFriendList);
    }

    public class Listener implements Click{

        @Override
        public void onClick(final String ID, String s) {
            if(s.equals("DongY")){
                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                mFriendsDatabase.child(mUserID).child(ID)
                        .child("date")
                        .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mFriendsDatabase.child(ID).child(mUserID)
                                .child("date")
                                .setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mRequestsFriendData.child(mUserID).child(ID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mRequestsFriendData.child(ID).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });

                                    }
                                });
                            }
                        });

                    }
                });

            }
            if(s.equals("TuChoi")){
                mRequestsFriendData.child(mUserID).child(ID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mRequestsFriendData.child(ID).child(mUserID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                
                            }
                        });

                    }
                });

            }
        }
    }

}