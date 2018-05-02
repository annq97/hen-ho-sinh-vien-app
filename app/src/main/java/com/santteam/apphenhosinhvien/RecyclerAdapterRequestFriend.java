package com.santteam.apphenhosinhvien;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nqait97 on 05-Nov-17.
 */

interface Click{
    void onClick(String ID, String s);
}

public class RecyclerAdapterRequestFriend extends RecyclerView.Adapter<RecyclerAdapterRequestFriend.ViewHolder>{

    private ArrayList<RequestsFriend> requestsFriends;
    private Context context;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mRequestsFriendDatabase;
    private FirebaseUser mCurrentUser;
    private Click listener;

    public RecyclerAdapterRequestFriend(ArrayList<RequestsFriend> requestsFriends, Context context,Click listener) {
        this.requestsFriends = requestsFriends;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.request_user_single_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(requestsFriends.get(position).getID());
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
        mRequestsFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friend_requests");

        final String mUserID = requestsFriends.get(position).getID();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("username").getValue().toString();
                String linkAnhDaiDien = dataSnapshot.child("anhdaidien").getValue().toString();

                holder.mUsername.setText(userName);
                Picasso.with(context).load(linkAnhDaiDien).placeholder(R.drawable.img_add_default).into(holder.mAnhDaiDien);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToTrangCaNhan(mUserID);
            }
        });

        holder.mDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mUserID,"DongY");
            }
        });

        holder.mTuChoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mUserID,"TuChoi");
            }
        });


    }

    private void SendToTrangCaNhan(String id) {
        Intent trangCaNhanIntent = new Intent(context,TrangCaNhanActivity.class);
        trangCaNhanIntent.putExtra("ID",id);
        context.startActivity(trangCaNhanIntent);
    }

    @Override
    public int getItemCount() {
        return requestsFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView mAnhDaiDien;
        TextView mUsername;
        Button mDongY,mTuChoi;
        View mView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mAnhDaiDien = itemView.findViewById(R.id.imgAnhDaiDien);
            mUsername = itemView.findViewById(R.id.tvUsername);
            mDongY = itemView.findViewById(R.id.btnDongY);
            mTuChoi = itemView.findViewById(R.id.btnTuChoi);
        }
    }

}

