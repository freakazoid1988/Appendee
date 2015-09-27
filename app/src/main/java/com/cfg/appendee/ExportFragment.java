package com.cfg.appendee;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cfg.appendee.database.AppenDB;
import com.cfg.appendee.database.DatabaseContract;
import com.cfg.appendee.objects.Participant;

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

    private OnFragmentInteractionListener mListener;

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
    public static ExportFragment newInstance(int param1) {
        ExportFragment fragment = new ExportFragment();
        Bundle args = new Bundle();
        args.putInt("eventID", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ID = getArguments().getInt("eventID");
            tablename = "\"" + Integer.toString(ID) + "\"";
        }

        startNewAsyncTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_export, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

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
            Cursor c = db.rawQuery(DatabaseContract.fetchParticipants(tablename), null);
            if (c.moveToFirst()) {
                do {
                    Participant p = new Participant();
                    p.setNumber(c.getInt(c.getColumnIndex(DatabaseContract.NUMBER)));
                    p.setEntrata(c.getInt(c.getColumnIndex(DatabaseContract.INGRESSO)) * 1000);
                    p.setUscita(c.getInt(c.getColumnIndex(DatabaseContract.USCITA)) * 1000);
                    participants.add(p);
                } while (c.moveToNext());
            }
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

}
