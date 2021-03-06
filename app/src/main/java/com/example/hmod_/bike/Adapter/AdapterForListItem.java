package com.example.hmod_.bike.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hmod_.bike.R;
import com.example.hmod_.bike.Rent;

import java.util.ArrayList;

public class AdapterForListItem extends RecyclerView.Adapter<AdapterForListItem.MyViewHolder> {
    private final ArrayList<Rent> rents;
    private final Context context;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public AdapterForListItem(ArrayList<Rent> rents, Context context, OnItemClickListener onItemClickListener) {
        this.rents = rents;
        this.context = context;
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
//        Picasso.with(context).load(quiz_image.get(position)).into(holder.mImageView);
        holder.mtime.setText(rents.get(position).getDuration());
        holder.mdate_and_price.setText(rents.get(position).getStartTimeAndCost());

    }

    @Override
    public int getItemCount() {
        return rents.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        final TextView mtime;
        final TextView mdate_and_price;

        MyViewHolder(View itemView) {
            super(itemView);
            mtime = itemView.findViewById(R.id.time);
            mdate_and_price = itemView.findViewById(R.id.date_and_price);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onItemClickListener.onListItemClick(clickedPosition);
        }
    }


}
