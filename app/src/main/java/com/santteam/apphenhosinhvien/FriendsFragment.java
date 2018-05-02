package com.santteam.apphenhosinhvien;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mLikesDatabase;
    private String mUserID;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_friends, container, false);
        addControls();

        mAuth = FirebaseAuth.getInstance();
        mUserID = mAuth.getCurrentUser().getUid();

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(mUserID);
        mFriendDatabase.keepSynced(true);

        mLikesDatabase = FirebaseDatabase.getInstance().getReference().child("likes").child(mUserID);
        mLikesDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    private void addControls() {
        mFriendsList = mMainView.findViewById(R.id.rvFriendsList);
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query =mFriendDatabase
                .limitToLast(50);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        };
        query.addChildEventListener(childEventListener);

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(query, Friends.class)
                        .build();

        FirebaseRecyclerAdapter friendsAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_single_layout, parent, false);

                return new FriendsViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(final FriendsViewHolder holder, int position, final Friends model) {

                holder.mDate.setText( model.getDate());

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        String linkAnhDaiDien = dataSnapshot.child("anhdaidien").getValue().toString();

                        holder.mUsername.setText(userName);
                        Picasso.with(getContext()).load(linkAnhDaiDien).placeholder(R.drawable.img_add_default).into(holder.mAnhDaiDien);

                        if(dataSnapshot.hasChild("online")) {
                            String mUserOnline = dataSnapshot.child("online").getValue().toString();
                            if (mUserOnline.equals("true")) {
                                holder.mUserOnline.setVisibility(View.VISIBLE);
                            } else {
                                holder.mUserOnline.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mLikesDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() != null) {
                            holder.mUserLike.setVisibility(View.VISIBLE);
                        }
                        else {
                                holder.mUserLike.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SendToTrangCaNhan(list_user_id);
                    }
                });


            }

        };
        friendsAdapter.startListening();
        mFriendsList.setAdapter(friendsAdapter);
    }

    private void SendToTrangCaNhan(String ID) {
        Intent trangCaNhanIntent = new Intent(getActivity(),TrangCaNhanActivity.class);
        trangCaNhanIntent.putExtra("ID",ID);
        startActivity(trangCaNhanIntent);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        TextView mUsername,mDate;
        CircleImageView mAnhDaiDien;
        ImageView mUserOnline,mUserLike;
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAnhDaiDien = itemView.findViewById(R.id.imgAnhDaiDien);
            mUsername = itemView.findViewById(R.id.tvUsername);
            mDate = itemView.findViewById(R.id.tvDate);
            mUserOnline = itemView.findViewById(R.id.imgUserOnline);
            mUserLike = itemView.findViewById(R.id.imgLike);
        }
    }
}
