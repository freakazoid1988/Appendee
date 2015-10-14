package com.cfg.appendee;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cfg.appendee.database.AppenDB;
import com.cfg.appendee.database.DatabaseContract;
import com.cfg.appendee.objects.Event;
import com.cfg.appendee.objects.MyAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectEventFragment.OnSelectEventFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectEventFragment extends Fragment {
    //ArrayAdapter<Event> eventArrayAdapter;

    private static final String TAG = "SelectEventFragment";
    private OnSelectEventFragmentInteractionListener mListener;
    private WeakReference<RetrieveAllEventsTask> asyncTaskWeakRef;
    private RecyclerView mRecyclerView;
    //private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    public SelectEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelectEventFragment.
     */
    public static SelectEventFragment newInstance() {
        SelectEventFragment fragment = new SelectEventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //startNewAsyncTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_event, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        ArrayList<Event> arrayList = new ArrayList<Event>();
        //eventArrayAdapter = new ArrayAdapter<Event>(getActivity(), R.layout.row, arrayList);

        /*listView.setClickable(true);
        listView.setLongClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onSelectEventInteraction(eventArrayAdapter.getItem(i).getID());
                Toast.makeText(getActivity().getBaseContext(), eventArrayAdapter.getItem(i).toString(), Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity().getBaseContext(), "TODO, sorry ^^", Toast.LENGTH_LONG).show(); //TODO
                return false;
            }
        });

        listView.setAdapter(eventArrayAdapter);*/


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getActivity(), arrayList);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setClickable(true);
        startNewAsyncTask();
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSelectEventFragmentInteractionListener) activity;
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
    public interface OnSelectEventFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSelectEventInteraction(int id);
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
            db.close();
            return events;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> events) {
            if (!events.isEmpty()) {
                mAdapter.addAll(events);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}
