package com.cfg.appendee;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private TextView result_textView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //LayoutInflater lf = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        result_textView = (TextView) rootView.findViewById(R.id.result_textView);
        result_textView.setTextSize(18);
        return rootView;
    }

    public void setText(String s) {
        result_textView.setText(s);
    }
}
