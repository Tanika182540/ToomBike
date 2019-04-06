package com.example.tanika.toombikeapplication.Recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tanika.toombikeapplication.R;

public class HistoryHolder extends RecyclerView.ViewHolder {

    TextView bikeSign,date,state,status,price;

    public HistoryHolder(View itemView) {
        super(itemView);

        bikeSign = (TextView)itemView.findViewById(R.id.bikeSignHistory);
        date = (TextView)itemView.findViewById(R.id.dateFix);
        state = (TextView)itemView.findViewById(R.id.bikeState);
        status = (TextView)itemView.findViewById(R.id.bikeStatus);
        price = (TextView)itemView.findViewById(R.id.price);
    }
}
