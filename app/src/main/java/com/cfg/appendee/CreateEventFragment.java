package com.cfg.appendee;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cfg.appendee.database.AppenDB;
import com.cfg.appendee.database.DatabaseContract;

import java.lang.ref.WeakReference;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText editTextNome, editTextLuogo, editTextData;
    private Button confirmButton;

    private WeakReference<CreateEventTask> asyncTaskWeakRef;

    private OnFragmentInteractionListener mListener;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateEventFragment newInstance(String param1, String param2) {
        CreateEventFragment fragment = new CreateEventFragment();
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

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_event, container, false);
        editTextNome = (EditText) rootView.findViewById(R.id.editTextNome);
        editTextLuogo = (EditText) rootView.findViewById(R.id.editTextLuogo);
        editTextData = (EditText) rootView.findViewById(R.id.editTextData);

        confirmButton = (Button) rootView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    private void attemptRegistration() {
        String nome = editTextNome.getText().toString();
        String luogo = editTextLuogo.getText().toString();
        String data = editTextData.getText().toString();

        if(nome==null){
            return;
        }
        if(nome==""){
            return;
        }
        if(luogo==null){
            return;
        }
        if(luogo==""){
            return;
        }
        if(data==null){
            return;
        }
        if(data==""){
            return;
        }

        startNewAsyncTask(nome, data, luogo);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onCreateEventInteraction(uri);
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

    private void startNewAsyncTask(String nome, String data, String luogo){
        CreateEventTask createEventTask = new CreateEventTask(nome, data, luogo, this.getActivity());
        this.asyncTaskWeakRef = new WeakReference<CreateEventTask>(createEventTask);
        createEventTask.execute();
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
        public void onCreateEventInteraction(Uri uri);
    }

    private class CreateEventTask extends AsyncTask<Void, Void, Boolean>{
        private final String nome, luogo, data;
        private Context context;

        CreateEventTask(String nome, String luogo, String data, Context context){
            this.nome=nome;
            this.data=data;
            this.luogo=luogo;
            this.context = context;
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            AppenDB mDbHelper = new AppenDB(context);
            final long insert;

            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.NAME, nome);
            values.put(DatabaseContract.PLACE, luogo);
            values.put(DatabaseContract.DATE, data);
            try{

                insert = db.insert(DatabaseContract.Events, null, values);
            }catch(SQLiteException sqle){
                return false;
            }
            try {
                db.execSQL(DatabaseContract.createEvent(insert));
            }catch(SQLiteException sqle){
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success){
            if(success){
                Fragment f = new ScanningFragment();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.container, f).commit();
            }
        }
    }

}
