package com.example.tanika.toombikeapplication.Recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tanika.toombikeapplication.R;

public class AddBikeHolder extends RecyclerView.ViewHolder {

    TextView signId , bikeModel;
    public AddBikeHolder(View itemView) {
        super(itemView);

        signId = (TextView)itemView.findViewById(R.id.signId);
        bikeModel = (TextView)itemView.findViewById(R.id.bikeModel);
    }
}
