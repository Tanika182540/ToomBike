package com.example.tanika.toombikeapplication.Recycler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tanika.toombikeapplication.CallService;
import com.example.tanika.toombikeapplication.R;
import com.example.tanika.toombikeapplication.ServiceDataActivity;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder>{

    private ArrayList<HistoryItem> mHistoryItems ;

    private Context context;
    public ItemClickListener itemClickListener;
    private View v;
    private String hisNum;

    public void  setOnItemClickListener(ItemClickListener listener){

        itemClickListener = listener;

    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        v = layoutInflater.inflate(R.layout.historymodel,parent,false);


        return new HistoryViewHolder(v);
    }

    public HistoryAdapter(ArrayList<HistoryItem> historyItems,Context context){

        mHistoryItems = historyItems;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        HistoryItem currentItem = mHistoryItems.get(position);

        holder.bikeSign.setText(currentItem.getBikeSign());
        holder.date.setText(currentItem.getDate());
        holder.bikeState.setText(currentItem.getBikeState());
        holder.price.setText(currentItem.getPrice());
        hisNum = currentItem.getHisNum();
        if(currentItem.getBikeStatus().equals("แจ้งซ่อม")){
            holder.bikeStatus.setTextColor(Color.rgb(228,1,1));
            holder.bikeStatus.setText(currentItem.getBikeStatus());
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if(isLongClick){
                        //Toast.makeText(context,"Long Click: "+ mHistoryItems.get(position).getBikeSign().toString(),Toast.LENGTH_SHORT).show();

                    }else {
                        //Toast.makeText(context," " +mHistoryItems.get(position).getHisNum().toString(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent (v.getContext(), CallService.class);
                        intent.putExtra("index",mHistoryItems.get(position).toString());
                        intent.putExtra("hisNum",mHistoryItems.get(position).getHisNum().toString());
                        context.startActivity(intent);
                    }
                }
            });
        }else if(currentItem.getBikeStatus().equals("กำลังดำเนินการ")){
            holder.bikeStatus.setTextColor(Color.rgb(255,151,14));
            holder.bikeStatus.setText(currentItem.getBikeStatus());
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Toast.makeText(context,"กำลังดำเนินการ",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(currentItem.getBikeStatus().equals("เสร็จสิ้น")){
            holder.bikeStatus.setTextColor(Color.rgb(18,178,0));
            holder.bikeStatus.setText(currentItem.getBikeStatus());
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    Toast.makeText(context,"เสร็จสิ้น",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#EEFED2"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
        }

    }

    @Override
    public int getItemCount() {
        return mHistoryItems.size();
    }

    public void addDataToTop(ArrayList<HistoryItem> mHistoryItemsNew){
        mHistoryItems.addAll(0,mHistoryItemsNew); // add data to top
    }
}
class HistoryViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener,View.OnLongClickListener{

    public ImageView imageView;
    public TextView bikeSign,date, bikeState,bikeStatus,price;

    private ItemClickListener itemClickListener;

    public HistoryViewHolder(View itemView){
        super(itemView);
        date = itemView.findViewById(R.id.dateFix);
        bikeSign = itemView.findViewById(R.id.bikeSignHistory);
        bikeState = itemView.findViewById(R.id.hisbikeState);
        bikeStatus = itemView.findViewById(R.id.bikeStatus);
        price = itemView.findViewById(R.id.price);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);


    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),true);
        return true;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
