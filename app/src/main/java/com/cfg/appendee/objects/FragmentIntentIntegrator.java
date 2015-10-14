package com.cfg.appendee.objects;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * Created by freakazoid on 13/10/15.
 */
public class FragmentIntentIntegrator extends IntentIntegrator {
    private final Fragment fragment;

    public FragmentIntentIntegrator(Fragment fragment) {
        super(fragment.getActivity());
        this.fragment = fragment;
    }

    @Override
    public void startActivityForResult(Intent intent, int code) {
        fragment.startActivityForResult(intent, code);
    }
}
