package com.alienhive.romancetracker;

import android.app.Fragment;
import android.support.wearable.view.CardFragment;

public class ActionTypeCardFragment extends CardFragment {

    public static Fragment newInstance(String actionName, String actionDescription) {
        Fragment fragment =  CardFragment.create(actionName, actionDescription);
        return fragment;
    }

    public ActionTypeCardFragment() {
        // Required empty public constructor
    }
}
