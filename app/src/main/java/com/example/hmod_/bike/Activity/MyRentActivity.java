package com.example.hmod_.bike.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hmod_.bike.Adapter.AdapterForListItem;
import com.example.hmod_.bike.R;
import com.example.hmod_.bike.Rent;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRentActivity extends Fragment implements AdapterForListItem.OnItemClickListener {

    private AdapterForListItem.OnItemClickListener onItemClickListener;

    private final ArrayList<Rent> rents = new ArrayList<>();

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_rent, container, false);
        Intent intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);

//        recyclerView = rootView.findViewById(R.id.recyclerView);
//        ButterKnife.bind(getActivity());
        onItemClickListener = this;


        initText();
        MainActivity.mainActivity.getSupportActionBar().setTitle("My Rents");
        return rootView;
    }

    private void initText() {
        MainActivity.db.collection("rents").whereEqualTo("uid", MainActivity.currentAuthUser.getUid()).orderBy("startTime", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            progressBar.setVisibility(View.GONE);
            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                Rent currentRent = documentChange.getDocument().toObject(Rent.class);
                rents.add(currentRent);
                initRecyclerView();
            }
        });


    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        AdapterForListItem mAdapter = new AdapterForListItem(rents, getContext(), onItemClickListener);
        recyclerView.setAdapter(mAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(getActivity(), " Thank You :) ", Toast.LENGTH_SHORT).show();
    }

}
