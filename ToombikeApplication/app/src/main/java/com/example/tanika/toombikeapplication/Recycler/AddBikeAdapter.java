package com.example.tanika.toombikeapplication.Recycler;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tanika.toombikeapplication.BottomNavMenu;
import com.example.tanika.toombikeapplication.MapsActivity;
import com.example.tanika.toombikeapplication.R;
import com.example.tanika.toombikeapplication.SetBikeActivity;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddBikeAdapter extends RecyclerView.Adapter<AddBikeViewHolder> {

    public Context context;
    String[] bikeSign,bikeModel;
    public ArrayList<AddBikeItem> mAddBikeItems ;
    private View v;
    public ItemClickListener itemClickListener;

    public void setOnItemClickListener(ItemClickListener listener){
        itemClickListener = listener;
    }


    @NonNull
    @Override
    public AddBikeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        v = layoutInflater.inflate(R.layout.addbikemodel,parent,false);

        return new AddBikeViewHolder(v);
    }

    public AddBikeAdapter(ArrayList<AddBikeItem> addBikeItems,Context context){
        mAddBikeItems = addBikeItems ;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull final AddBikeViewHolder holder, int position) {
        AddBikeItem currentItem = mAddBikeItems.get(position);

        holder.signId.setText(currentItem.getBikeSign());
        holder.bikeModel.setText(currentItem.getBikeBrand() + " " + currentItem.getBikeModel());

        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#FEF5D2"));
            //  holder.imageView.setBackgroundColor(Color.parseColor("#FFFAF8FD"));
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){

                }else {
                    //Toast.makeText(context," " +mAddBikeItems.get(position).getBikeSign().toString(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (v.getContext(), SetBikeActivity.class);
                    //intent.putExtra("index",mAddBikeItems.get(realPosition).toString());
                    intent.putExtra("name",mAddBikeItems.get(position).getUserName().toString());
                    intent.putExtra("bikeSign",mAddBikeItems.get(position).getBikeSign().toString());
                    intent.putExtra("phoneNum",mAddBikeItems.get(position).getUserPhone().toString());
                    intent.putExtra("bikeModel",mAddBikeItems.get(position).getBikeModel().toString());

                    Log.d("dataAdapter",mAddBikeItems.get(position).getUserName().toString());
                    context.startActivity(intent);

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mAddBikeItems.size();
    }

    public void removeItem(int position) {
        mAddBikeItems.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(AddBikeItem item, int position) {
        mAddBikeItems.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

}

class AddBikeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

    private ItemClickListener itemClickListener;
    public TextView signId, bikeModel;
    public ImageView delete;
    public LinearLayout linearMyBike ;
    public Context context;
    public CardView cardView;

    public AddBikeViewHolder(final View itemView) {
        super(itemView);

        signId = (TextView)itemView.findViewById(R.id.signId);
        bikeModel = (TextView)itemView.findViewById(R.id.bikeModel);
        //delete = (ImageView)itemView.findViewById(R.id.deleteImgView);
        //linearLayoutImgView = (LinearLayout)itemView.findViewById(R.id.linearImgView);
        cardView = (CardView)itemView.findViewById(R.id.model);
        linearMyBike = (LinearLayout)itemView.findViewById(R.id.linearMybike);


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

        return false;
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


}


