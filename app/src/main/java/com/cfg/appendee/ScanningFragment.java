package com.cfg.appendee;


import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cfg.appendee.database.AppenDB;
import com.cfg.appendee.database.DatabaseContract;
import com.github.clans.fab.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScanningFragment extends Fragment implements View.OnClickListener {

    private static final int REGISTRA_ENTRATA = 1, REGISTRA_USCITA = 2;
    private TextView result_textView;
    private FloatingActionButton actionButton;
    private LinearLayout linearLayoutRegistraButtons;
    private Button registraEntrataButton, registraUscitaButton;
    private int ID;
    private String s, tablename;
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

        View rootView = inflater.inflate(R.layout.fragment_scanning, container, false);

        if (getArguments() != null) {
            ID = getArguments().getInt("eventID");
            tablename = "\"" + Integer.toString(ID) + "\"";
        }

        result_textView = (TextView) rootView.findViewById(R.id.result_textView);
        result_textView.setTextSize(24);

        result_textView.setText("Benvenuto! Inizia la scansione, ebbreo.");

        actionButton = (FloatingActionButton) rootView.findViewById(R.id.action_button);
        actionButton.setImageResource(R.drawable.fab_add);
        actionButton.setColorNormal(Color.RED);
        actionButton.setOnClickListener(this);

        registraEntrataButton = (Button) rootView.findViewById(R.id.inButton);
        registraEntrataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registra(REGISTRA_ENTRATA);
            }
        });
        registraUscitaButton = (Button) rootView.findViewById(R.id.outButton);
        registraUscitaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registra(REGISTRA_USCITA);
            }
        });

        linearLayoutRegistraButtons = (LinearLayout) rootView.findViewById(R.id.linearLayoutRegistraButtons);
        linearLayoutRegistraButtons.setVisibility(View.INVISIBLE);

        return rootView;
    }

    private void registra(int inOrOut) {
        AppenDB mDBHelper = new AppenDB(getActivity());
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        switch (inOrOut) {
            case REGISTRA_ENTRATA:
                cv.put(DatabaseContract.NUMBER, Integer.parseInt(s));
                cv.put(DatabaseContract.INGRESSO, System.currentTimeMillis() / 1000);
                try {
                    long newRowId = db.insert(tablename, null, cv);
                } catch (SQLiteException sqle) {
                    System.out.println(Integer.parseInt(s) + " " + System.currentTimeMillis() / 1000);
                    sqle.printStackTrace();
                }
                db.close();
                break;

            case REGISTRA_USCITA:
                cv.put(DatabaseContract.USCITA, System.currentTimeMillis() / 1000);
                String where = DatabaseContract.NUMBER + "=" + s;
                int count = db.update(tablename, cv, where, null);
                db.close();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // Parsing bar code reader result
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result != null) {
                        s = result.getContents();
                        setText("Ho scansionato il codice " + s + ", fai click su \"Registra Entrata\" o \"Registra uscita\" per salvarlo nel database");
                        linearLayoutRegistraButtons.setVisibility(View.VISIBLE);
                    }

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
