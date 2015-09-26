package com.cfg.appendee;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cfg.appendee.database.AppenDB;
import com.cfg.appendee.database.DatabaseContract;
import com.cfg.appendee.objects.Event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayAdapter<Event> eventArrayAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private WeakReference<RetrieveAllEventsTask> asyncTaskWeakRef;
    private ListView listView;

    public SelectEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectEventFragment newInstance(String param1, String param2) {
        SelectEventFragment fragment = new SelectEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        RetrieveAllEventsTask retrieveAllEventsTask = new RetrieveAllEventsTask(getActivity());
        retrieveAllEventsTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_event, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        ArrayList<Event> arrayList = new ArrayList<Event>();
        eventArrayAdapter = new ArrayAdapter<Event>(getActivity(), R.layout.row, arrayList);

        listView.setClickable(true);
        listView.setLongClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onSelectEventInteraction(eventArrayAdapter.getItem(i).getID());
                Toast.makeText(getActivity().getBaseContext(), eventArrayAdapter.getItem(i).toString(), Toast.LENGTH_LONG).show();
            }
        });

        listView.setAdapter(eventArrayAdapter);

        return rootView;
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

    /*@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mListener.onSelectEventInteraction(eventArrayAdapter.getItem(i).getID());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }*/

    private void startNewAsyncTask() {
        RetrieveAllEventsTask retrieveAllEventsTask = new RetrieveAllEventsTask(this.getActivity());
        this.asyncTaskWeakRef = new WeakReference<RetrieveAllEventsTask>(retrieveAllEventsTask);
        retrieveAllEventsTask.execute();
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
        public void onSelectEventInteraction(int id);
    }

    private class RetrieveAllEventsTask extends AsyncTask<Void, Void, ArrayList<Event>> {

        private Context context;

        RetrieveAllEventsTask(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Event> doInBackground(Void... voids) {
            ArrayList events = new ArrayList<Event>();
            AppenDB mDbHelper = new AppenDB(context);

            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            Cursor c = db.rawQuery(DatabaseContract.FETCH_EVENTS, null);
            if (c.moveToFirst()) {
                do {
                    Event e = new Event();
                    e.setID(c.getInt(c.getColumnIndex(DatabaseContract._ID1)));
                    e.setName(c.getString(c.getColumnIndex(DatabaseContract.NAME)));
                    e.setLocation(c.getString(c.getColumnIndex(DatabaseContract.PLACE)));
                    int date = c.getInt(c.getColumnIndex(DatabaseContract.DATE));
                    Date d = new Date((long) date * 1000);
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(d);
                    e.setDate(gc);
                    events.add(e);
                } while (c.moveToNext());
            }
            return events;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> events) {
            if (!events.isEmpty()) {
                eventArrayAdapter.addAll(events);
                eventArrayAdapter.notifyDataSetChanged();
            }
        }
    }

}
