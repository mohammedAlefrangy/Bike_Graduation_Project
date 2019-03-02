package com.example.hmod_.bike.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hmod_.bike.R;

import java.util.ArrayList;

public class AdapterForListItem extends RecyclerView.Adapter<AdapterForListItem.MyViewHolder> {
    private ArrayList<String> time;
    private ArrayList<String> date_and_price;
    private Context context;
    private final OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }


    public AdapterForListItem(ArrayList<String> time, ArrayList<String> date_and_price, Context context, OnItemClickListener onItemClickListener) {
        this.time = time;
        this.date_and_price = date_and_price;
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
        holder.mtime.setText(time.get(position));
        holder.mdate_and_price.setText(date_and_price.get(position));

    }

    @Override
    public int getItemCount() {
        return time.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        TextView mtime, mdate_and_price;

        public MyViewHolder(View itemView) {
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
