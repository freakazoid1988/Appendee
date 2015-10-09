package com.cfg.appendee;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cfg.appendee.database.AppenDB;
import com.cfg.appendee.database.DatabaseContract;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


/**
 * A placeholder fragment containing a simple view.
 */
public class ScanningFragment extends Fragment implements View.OnClickListener {

    private static final int REGISTRA_ENTRATA = 1, REGISTRA_USCITA = 2;
    private static int inOrOut;
    private OnScanningFragmentInteractionListener mListener;
    private TextView result_textView;
    private FloatingActionButton scanCode, exportToExcel;
    private FloatingActionMenu actionMenu;
    private RadioButton registraEntrataButton, registraUscitaButton;
    private RadioGroup radioGroup;
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
            tablename = "T" + Integer.toString(ID);
        }

        result_textView = (TextView) rootView.findViewById(R.id.result_textView);
        result_textView.setTextSize(18);

        result_textView.setText("Benvenuto! Inizia la scansione.");

        actionMenu = (FloatingActionMenu) rootView.findViewById(R.id.action_menu);

        scanCode = (FloatingActionButton) rootView.findViewById(R.id.scan_code);
        scanCode.setColorNormal(R.color.button_material_light);
        scanCode.setColorPressed(R.color.primary_text_disabled_material_light);
        scanCode.setOnClickListener(this);

        exportToExcel = (FloatingActionButton) rootView.findViewById(R.id.export_to_excel);
        exportToExcel.setColorNormal(R.color.button_material_light);
        exportToExcel.setColorPressed(R.color.primary_text_disabled_material_light);
        exportToExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onExportEventInteraction(tablename);
            }
        });

        radioGroup = (RadioGroup) rootView.findViewById(R.id.radioGroupRegistraButtons);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.inButton:
                        inOrOut = REGISTRA_ENTRATA;
                        break;
                    case R.id.outButton:
                        inOrOut = REGISTRA_USCITA;
                        break;
                }
            }
        });

        radioGroup.check(R.id.inButton);
        inOrOut = REGISTRA_ENTRATA;
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnScanningFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnScanningFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void registra(int inOrOut) {
        AppenDB mDBHelper = new AppenDB(getActivity());
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();

        final ContentValues cv = new ContentValues();

        switch (inOrOut) {
            case REGISTRA_ENTRATA:

                cv.put(DatabaseContract.NUMBER, Integer.parseInt(s));
                cv.put(DatabaseContract.INGRESSO, System.currentTimeMillis() / 1000);

                if (isAlreadyInDb(s, db)) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String where = DatabaseContract.NUMBER + " = " + s;
                            int count = db.update(tablename, cv, where, null);
                        }
                    }).setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).setMessage("Attenzione! L'utente è già stato registrato in entrata. Sovrascrivere?").show();
                } else {
                    try {
                        long newRowId = db.insert(tablename, null, cv);
                        Toast.makeText(getActivity(), "Registrato il numero " + s, Toast.LENGTH_LONG).show();
                    } catch (SQLiteException sqle) {
                        System.out.println(Integer.parseInt(s) + " " + System.currentTimeMillis() / 1000);
                        sqle.printStackTrace();
                    }
                }

                break;

            case REGISTRA_USCITA:
                cv.put(DatabaseContract.USCITA, System.currentTimeMillis() / 1000);
                String where = DatabaseContract.NUMBER + "=" + s;
                int count = db.update(tablename, cv, where, null);
                break;
        }
    }

    private boolean isAlreadyInDb(String s, SQLiteDatabase db) {
        String query = "SELECT * FROM " + tablename + " WHERE " + DatabaseContract.NUMBER + " = " + s;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.scan_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
                        setText("Ho scansionato il codice " + s);
                        registra(inOrOut);
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
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.deleteEventMenuVoice:
                DeleteEventTask deleteEventTask = new DeleteEventTask(getActivity(), tablename);
                deleteEventTask.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnScanningFragmentInteractionListener {
        // TODO: Update argument type and name
        void onExportEventInteraction(String tablename);
    }

    private class DeleteEventTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private String tablename;

        public DeleteEventTask(Context context, String tablename) {
            this.context = context;
            this.tablename = tablename;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            AppenDB mDbHelper = new AppenDB(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            AppenDB.deleteTable(db, ID);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Fragment f = SelectEventFragment.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.container, f).commit();
        }
    }
}
