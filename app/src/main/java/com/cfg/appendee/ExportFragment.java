package com.cfg.appendee;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cfg.appendee.database.AppenDB;
import com.cfg.appendee.database.DatabaseContract;
import com.cfg.appendee.objects.Participant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExportFragment extends Fragment {
    ArrayAdapter<Participant> participantsArrayAdapter;
    private int ID;
    private String tablename;
    private WeakReference<RetrieveParticipantsTask> asyncTaskWeakRef;
    private ListView listView;
    private Button exportButton;

    //private OnFragmentInteractionListener mListener;

    public ExportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ExportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExportFragment newInstance(String param1) {
        ExportFragment fragment = new ExportFragment();
        Bundle args = new Bundle();
        args.putString("eventIDString", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //ID = getArguments().getInt("eventID");
            tablename = getArguments().getString("eventIDString");
        }

        startNewAsyncTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_export, container, false);
        listView = (ListView) rootView.findViewById(R.id.listViewExport);
        final ArrayList<Participant> arrayList = new ArrayList<Participant>();
        participantsArrayAdapter = new ArrayAdapter<Participant>(getActivity(), R.layout.row, arrayList);
        listView.setAdapter(participantsArrayAdapter);

        exportButton = (Button) rootView.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isExternalStorageWritable()) {
                    ExportParticipantsToExcelFile exportParticipantsToExcelFile = new ExportParticipantsToExcelFile(getActivity());
                    exportParticipantsToExcelFile.execute(arrayList);
                } else {
                    System.out.println("Porcamadonna accattati 'na cazzo di scheda SD!"); //TODO
                }
            }
        });

        return rootView;
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void startNewAsyncTask() {
        RetrieveParticipantsTask retrieveParticipantsTask = new RetrieveParticipantsTask(this.getActivity());
        this.asyncTaskWeakRef = new WeakReference<RetrieveParticipantsTask>(retrieveParticipantsTask);
        retrieveParticipantsTask.execute();
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
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String tablename);
    }*/

    private class RetrieveParticipantsTask extends AsyncTask<Void, Void, ArrayList<Participant>> {

        private Context context;

        RetrieveParticipantsTask(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Participant> doInBackground(Void... voids) {
            ArrayList participants = new ArrayList<Participant>();
            AppenDB mDbHelper = new AppenDB(context);

            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = db.rawQuery(AppenDB.fetchParticipants(tablename), null);
            if (c.moveToFirst()) {
                do {
                    Participant p = new Participant();
                    p.setNumber(c.getInt(c.getColumnIndex(DatabaseContract.NUMBER)));
                    p.setEntrata(c.getInt(c.getColumnIndex(DatabaseContract.INGRESSO)));
                    p.setUscita(c.getInt(c.getColumnIndex(DatabaseContract.USCITA)));
                    participants.add(p);
                } while (c.moveToNext());
            }
            db.close();
            return participants;
        }

        @Override
        protected void onPostExecute(ArrayList<Participant> participants) {
            if (!participants.isEmpty()) {
                participantsArrayAdapter.addAll(participants);
                participantsArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ExportParticipantsToExcelFile extends AsyncTask<ArrayList<Participant>, Void, Boolean> {
        private Context context;

        ExportParticipantsToExcelFile(Context context) {
            this.context = context;
        }


        @Override
        protected Boolean doInBackground(ArrayList<Participant>... arrayLists) {
            ArrayList<Participant> arrayListInTask = arrayLists[0];
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Skappa";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(context.getExternalFilesDir(null), tablename + ".txt");
            //File file = new File(path, tablename + ".txt"); // Conviene caricare in una cartella esterna?

            try {
                PrintWriter pw = new PrintWriter(file);
                for (short i = 0; i < arrayListInTask.size(); i++) {
                    pw.println(arrayListInTask.get(i).toString());
                }
                pw.flush();
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public void onPostExecute(final Boolean success) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    // The intent does not have a URI, so declare the "text/plain" MIME type
                    emailIntent.setType("text/plain");
                    /*emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");*/
                    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + context.getExternalFilesDir(null) + "/" + tablename + ".txt"));
                    startActivity(emailIntent);
                }
            }).setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setIcon(android.R.drawable.ic_dialog_info).setMessage("Per una volta è filato tutto liscio, vuoi inviare il file per email?").show();
        }
    }

}
