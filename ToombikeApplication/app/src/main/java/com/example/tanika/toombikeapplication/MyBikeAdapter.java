package com.example.tanika.toombikeapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyBikeAdapter extends RecyclerView.Adapter<MyBikeAdapter.MyBikeViewHolder> {

    private ArrayList<Bikeitem> mbikeItemList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class MyBikeViewHolder extends RecyclerView.ViewHolder{

        public ImageView mImageView ;
        public TextView mTextView1;
        public TextView mTextView2;

        public MyBikeViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyBikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bikeitem,parent,false);
        MyBikeViewHolder bvh = new MyBikeViewHolder(v,mListener);
        return bvh;
    }

    public MyBikeAdapter(ArrayList<Bikeitem> bikeItemList){
        mbikeItemList = bikeItemList;
    }

    @Override
    public void onBindViewHolder(@NonNull MyBikeViewHolder holder, int position) {

        Bikeitem currentBike = mbikeItemList.get(position);

        holder.mImageView.setImageResource(currentBike.getmImageResource());
        holder.mTextView1.setText(currentBike.getText1());
        holder.mTextView2.setText(currentBike.getText2());
    }

    @Override
    public int getItemCount() {
        return mbikeItemList.size();
    }
}
