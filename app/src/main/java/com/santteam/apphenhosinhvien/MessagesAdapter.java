package com.santteam.apphenhosinhvien;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santteam.apphenhosinhvien.model.ImageFullSCActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by nqait97 on 15-Nov-17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Messages> mMessagesList;
    private ChatActivity context;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;


    public MessagesAdapter(ChatActivity context,List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
        this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_single_message,
                parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        holder.messageTextKhach.setUseSystemDefault(true);
        holder.messageTextMe.setUseSystemDefault(true);
        String mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Messages c = mMessagesList.get(position);
        final String fromUser = c.getFrom();
        final String message_type = c.getType();
        if(fromUser.equals(mCurrentUserID)){

            holder.rvMessageChatKhachMe.setVisibility(View.VISIBLE);
            holder.rvMessageChatKhach.setVisibility(View.GONE);
            holder.messageImageMe.setVisibility(View.VISIBLE);
            holder.messageTextMe.setVisibility(View.VISIBLE);
            if(message_type.equals("text")){
                holder.messageImageMe.setVisibility(View.GONE);
                holder.messageTextMe.setText(c.getMessage());
            }else{
                holder.messageTextMe.setVisibility(View.GONE);
                Picasso.with(context).load(c.getMessage()).placeholder(R.drawable.img_add_default).into(holder.messageImageMe);
            }
            holder.messageImageMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentToImgFull(c.getMessage());
                }
            });
        }else{
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dt : dataSnapshot.getChildren()){
                        if(dt.getKey().equals(fromUser)){
                            Picasso.with(context).load(dt.child("anhdaidien").getValue().toString()).placeholder(R.drawable.img_add_default).into(holder.messageAnhDaiDienKhach);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            holder.rvMessageChatKhachMe.setVisibility(View.GONE);
            holder.rvMessageChatKhach.setVisibility(View.VISIBLE);
            holder.messageImageKhach.setVisibility(View.VISIBLE);
            holder.messageTextKhach.setVisibility(View.VISIBLE);

            if(message_type.equals("text")){
                holder.messageImageKhach.setVisibility(View.GONE);
                holder.messageTextKhach.setText(c.getMessage());
            }else{
                holder.messageTextKhach.setVisibility(View.GONE);
                Picasso.with(context).load(c.getMessage()).placeholder(R.drawable.img_add_default).into(holder.messageImageKhach);
            }
            holder.messageImageKhach.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sentToImgFull(c.getMessage());
                }
            });
        }




    }

    private void sentToImgFull(String message) {
        Intent imgcChatFullIntent = new Intent(context, ImageFullSCActivity.class);
        imgcChatFullIntent.putExtra("linkanhdaidien",message);
        context.startActivity(imgcChatFullIntent);
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public EmojiconTextView messageTextKhach,messageTextMe;
        public CircleImageView messageAnhDaiDienKhach;
        public RelativeLayout rvMessageChatKhach,rvMessageChatKhachMe;
        private ImageView messageImageKhach,messageImageMe;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextKhach = itemView.findViewById(R.id.tvMessageSingleKhach);
            messageTextMe = itemView.findViewById(R.id.tvMessageSingleMe);
            messageAnhDaiDienKhach = itemView.findViewById(R.id.imgAnhDaiDienKhach);
            rvMessageChatKhach  = itemView.findViewById(R.id.rvChatKhach);
            rvMessageChatKhachMe = itemView.findViewById(R.id.rvChatMe);
            messageImageKhach = itemView.findViewById(R.id.message_image_chat_khach);
            messageImageMe = itemView.findViewById(R.id.message_image_chat_me);
        }
    }
}
