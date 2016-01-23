package com.alienhive.romancetracker.domain;

import android.content.Context;

import com.alienhive.romancetracker.ActionButtonFragment;

public class ActionColumn {
    private ActionButtonFragment fragment;

    public ActionColumn()
    {
        fragment = ActionButtonFragment.newFragment();
    }

    public ActionButtonFragment getFragment() {
        return fragment;
    }
}
