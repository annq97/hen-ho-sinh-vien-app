package com.santteam.apphenhosinhvien;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mRequestsChatList;
    private View mMainView;
    private DatabaseReference mRequestsChatDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mUserID;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView =  inflater.inflate(R.layout.fragment_requests, container, false);
        addControls();

        mAuth = FirebaseAuth.getInstance();
        mUserID = mAuth.getCurrentUser().getUid();

        mRequestsChatDatabase = FirebaseDatabase.getInstance().getReference().child("chat_requests").child(mUserID);
        mRequestsChatDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mRequestsChatList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        mRequestsChatList.setLayoutManager(layoutManager);

        return mMainView;
    }

    private void addControls() {
        mRequestsChatList = mMainView.findViewById(R.id.rvListChat);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query =mRequestsChatDatabase
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

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(query, Chat.class)
                        .build();

        FirebaseRecyclerAdapter requestChatAdapter = new FirebaseRecyclerAdapter<Chat,RequestChatHolder>(options) {
            @Override
            public RequestChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_user_single, parent, false);

                return new RequestChatHolder(view);
            }

            @Override
            protected void onBindViewHolder(final RequestChatHolder holder, int position, Chat model) {
                holder.mLastMessage.setText( model.getLastmessage());

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = dataSnapshot.child("username").getValue().toString();
                        String linkAnhDaiDien = dataSnapshot.child("anhdaidien").getValue().toString();

                        holder.mUsername.setText(userName);
                        Picasso.with(getContext()).load(linkAnhDaiDien).placeholder(R.drawable.img_add_default).into(holder.mAnhDaiDien);
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

        requestChatAdapter.startListening();
        mRequestsChatList.setAdapter(requestChatAdapter);

    }

    private void SendToTrangCaNhan(String ID) {
        Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
        chatIntent.putExtra("ID",ID);
        startActivity(chatIntent);
    }

    public class RequestChatHolder extends RecyclerView.ViewHolder{
        TextView mUsername,mLastMessage;
        CircleImageView mAnhDaiDien;
        View mView;

        public RequestChatHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAnhDaiDien = itemView.findViewById(R.id.imgAnhDaiDien);
            mUsername = itemView.findViewById(R.id.tvUsername);
            mLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }
    }

}
