package com.cfg.appendee;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.software.shell.fab.ActionButton;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScanningFragment extends Fragment implements View.OnClickListener {

    private TextView result_textView;
    private ActionButton actionButton;
    private int ID;

    public ScanningFragment() {
    }

    public static ScanningFragment newInstance(int param1) {
        ScanningFragment scanningFragment = new ScanningFragment();
        Bundle args = new Bundle();
        args.putInt("eventID", param1);
        scanningFragment.setArguments(args);
        return scanningFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //LayoutInflater lf = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_scanning, container, false);

        if (getArguments() != null) {
            ID = getArguments().getInt("eventID");
        }

        result_textView = (TextView) rootView.findViewById(R.id.result_textView);
        result_textView.setTextSize(24);

        result_textView.setText("Benvenuto! Inizia la scansione, ebbreo.");

        actionButton = (ActionButton) rootView.findViewById(R.id.action_button);
        actionButton.setImageResource(R.drawable.fab_plus_icon);
        actionButton.setButtonColor(Color.RED);
        actionButton.setOnClickListener(this);

        return rootView;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String s = scanResult.getContents();
            setText("Ho scansionato il codice " + s + ", fai click su \"Registra Entrata\" o \"Registra uscita\" per salvarlo nel database");
        }
        // else continue with any other code you need in the method
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Parsing bar code reader result
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (true)
                        Log.d("Scan", "Parsing bar code reader result: " + result.toString()); //TODO

                }
                break;
        }
    }

    public void setText(String s) {
        result_textView.setText(s);
    }

    @Override
    public void onClick(View view) {
        IntentIntegrator.forFragment(this).initiateScan();
    }
}
