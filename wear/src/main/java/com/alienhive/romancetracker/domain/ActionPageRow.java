package com.alienhive.romancetracker.domain;

import android.app.Fragment;

import com.alienhive.romancetracker.ActionTypeFragment;

public class ActionPageRow {

    private Fragment actionTypeFragment;

    public Fragment getFragment() {
        return actionTypeFragment;
    }

    public ActionPageRow(String title, String description)
    {
        actionTypeFragment = ActionTypeFragment.newInstance(title, description);
    }
}
