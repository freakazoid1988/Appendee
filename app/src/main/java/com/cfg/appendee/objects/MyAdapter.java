package com.cfg.appendee.objects;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cfg.appendee.R;
import com.cfg.appendee.ScanningFragment;
import com.cfg.appendee.database.AppenDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by freakazoid on 08/10/15.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Event> mDataset;
    private FragmentActivity context;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(FragmentActivity context, List<Event> myDataset) {
        mDataset = myDataset;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.event_name.setText(mDataset.get(position).getName());
        holder.event_location.setText(mDataset.get(position).getLocation());
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = ScanningFragment.newInstance(mDataset.get(position).getID());
                context.getSupportFragmentManager().beginTransaction().replace(R.id.container, f).commit();
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteEventTask deleteEventTask = new DeleteEventTask(context, mDataset.get(position).getID(), position);
                deleteEventTask.execute();
                /*notifyItemRemoved(position);
                notifyDataSetChanged();*/
            }
        });
        holder.deleteButton.setVisibility(View.VISIBLE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addAll(ArrayList<Event> e) {
        mDataset.addAll(e);
        notifyDataSetChanged();
    }

    public void add(final Event object) {
        mDataset.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView event_name, event_location;
        public CardView cv;
        public ImageButton deleteButton;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView) v.findViewById(R.id.cv);
            event_name = (TextView) v.findViewById(R.id.event_name);
            event_location = (TextView) v.findViewById(R.id.event_location);
            deleteButton = (ImageButton) v.findViewById(R.id.deleteEventButton);
        }


    }

    private class DeleteEventTask extends AsyncTask<Void, Void, Boolean> {
        private Context context;
        private int ID, position;

        public DeleteEventTask(Context context, int ID, int position) {
            this.context = context;
            this.ID = ID;
            this.position = position;
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
            mDataset.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
        }
    }

}
