package com.example.hmod_.bike.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hmod_.bike.Adapter.AdapterForListItem;
import com.example.hmod_.bike.R;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRentActivity extends Fragment implements AdapterForListItem.OnItemClickListener {

    GridLayoutManager layoutManager;
    private AdapterForListItem mAdapter;
    private AdapterForListItem.OnItemClickListener onItemClickListener;

    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> date_and_price = new ArrayList<>();
    Intent intentThatStartedThisActivity ;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_rent, container, false);
        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);

//        recyclerView = rootView.findViewById(R.id.recyclerView);
//        ButterKnife.bind(getActivity());
        onItemClickListener = this;


        initText();

        return rootView;
    }

    private void initText() {
        time.add(" 1 Hour and 32 Minutes ");
        date_and_price.add(" 2019/01/04 01:08pm -- 2 NIS ");

        time.add(" 15 Minutes ");
        date_and_price.add(" 2019/01/11 10:17pm -- 1 NIS ");

        time.add(" 30 Minutes ");
        date_and_price.add(" 2019/01/7 11:32pm -- 4 NIS ");

        time.add(" 2 Hour and 15 Minutes ");
        date_and_price.add(" 2019/01/04 12:08pm -- 3 NIS ");


        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        mAdapter = new AdapterForListItem(time, date_and_price, getContext(), onItemClickListener);
        recyclerView.setAdapter(mAdapter);
        layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(getActivity(), " Thank You :) ", Toast.LENGTH_SHORT).show();
    }

}
