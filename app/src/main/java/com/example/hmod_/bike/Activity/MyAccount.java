package com.example.hmod_.bike.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hmod_.bike.R;
import com.example.hmod_.bike.SplachActivity;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.barcode.BarcodeReader;

public class MyAccount extends Fragment implements BarcodeReader.BarcodeReaderListener {

    Intent intentThatStartedThisActivity;
    private static final String TAG = "MyAccount";


    private BarcodeReader barcodeReader;
    @BindView(R.id.codeET)
    EditText codeET;
    @BindView(R.id.add)
    Button addBtn;
    @BindView(R.id.credits)
    TextView creditsTV;
    @BindView(R.id.userName)
    TextView userNameTV;
    @BindView(R.id.circleImageView)
    ImageView circleIV;
    public static MyAccount instance;

    public MyAccount() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_account, container, false);
//        intentThatStartedThisActivity = Objects.requireNonNull(getActivity()).getIntent();
        ButterKnife.bind(this, rootView);

        barcodeReader = (BarcodeReader) getChildFragmentManager().findFragmentById(R.id.barcode_fragment);
        barcodeReader.setListener(this);

        addBtn.setOnClickListener(view -> {
            Map<String, Object> data = new HashMap<>();
            data.put("voucher", codeET.getText().toString());
            MainActivity.ff.getHttpsCallable("updateCredits").call(data).addOnSuccessListener(httpsCallableResult -> {
                if (httpsCallableResult.getData() instanceof Map) {
                    Map<String, Object> dataObj = (Map<String, Object>) httpsCallableResult.getData();
                    if ((Boolean) dataObj.get("success")) {
                        codeET.setText("");
                        Toast.makeText(getActivity(), "Thanks you ;) Voucher added successfully.", Toast.LENGTH_SHORT).show();
                        SplachActivity.updateUser();
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
        });
        instance = this;
        updateUI();
        MainActivity.mainActivity.getSupportActionBar().setTitle("My Account");
        return rootView;

    }


    @Override
    public void onScanned(final Barcode barcode) {
        Log.e(TAG, "onScanned: " + barcode.displayValue);
        barcodeReader.playBeep();

        getActivity().runOnUiThread(() -> {
            //Toast.makeText(getActivity(), "Barcode: " + barcode.displayValue, Toast.LENGTH_SHORT).show();
            codeET.setText(barcode.displayValue);
        });
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {
        Log.e(TAG, "onScanError: " + errorMessage);
    }

    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getActivity(), "Camera permission denied!", Toast.LENGTH_LONG).show();
    }

    void updateUI() {
        userNameTV.setText(MainActivity.currentUser.getName());
        creditsTV.setText(String.format("Credits: %.2f NIS", MainActivity.currentUser.getCredits()));
        if (!MainActivity.currentUser.getPhoto().isEmpty())
            Picasso.get().load(MainActivity.currentUser.getPhoto()).into(circleIV);

    }

}
