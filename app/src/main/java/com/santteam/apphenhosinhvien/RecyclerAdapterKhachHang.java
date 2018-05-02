package com.santteam.apphenhosinhvien;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.santteam.apphenhosinhvien.model.KhachHang;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by nqait97 on 05-Nov-17.
 */

public class RecyclerAdapterKhachHang extends RecyclerView.Adapter<RecyclerAdapterKhachHang.ViewHolder>{

    private ArrayList<KhachHang> khachHangs;
    private TimKiemActivity context;

    public RecyclerAdapterKhachHang(ArrayList<KhachHang> khachHangs, TimKiemActivity context) {
        this.khachHangs = khachHangs;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list_anh_dai_dien_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Picasso.with(context).load(khachHangs.get(position).getAnhdaidien()).placeholder(R.drawable.img_add_default).into(holder.imgAnhdaidien);
        holder.imgAnhdaidien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.chuyenMH(khachHangs.get(position).getID());
            }
        });


    }

    @Override
    public int getItemCount() {
        return khachHangs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgAnhdaidien;
        public ViewHolder(View itemView) {
            super(itemView);
            imgAnhdaidien = itemView.findViewById(R.id.imgAnhDaiDien);
        }
    }

}

