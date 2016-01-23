package com.alienhive.romancetracker.domain;

import android.app.Fragment;

import com.alienhive.romancetracker.ActionTypeFragment;

import java.util.ArrayList;

public class ActionPageRow {

    //Private fields
    private Fragment actionTypeFragment;
    private ArrayList<ActionColumn> actionColumnList;

    /**
     * Constructor
     * @param title
     * @param description
     */
    public ActionPageRow(String title, String description)
    {
        setupActionTypeColumn(title, description);
        setupActionColumns();
    }

    public Fragment getFragment(int col) {
        if (col == 0) {
            return actionTypeFragment;
        }
        else if (col >= 1) {
            return actionColumnList.get(col - 1).getFragment();
        }
        else
        {
            return null;
        }
    }

    public int getColumnCount() {
        return actionColumnList.size() + 1;
    }

    private void setupActionTypeColumn(String title, String description) {
        actionTypeFragment = ActionTypeFragment.newInstance(title, description);
    }

    private void setupActionColumns() {
        actionColumnList = new ArrayList<>(2);
        ActionColumn actionColumn1 = new ActionColumn();
        actionColumnList.add(actionColumn1);
        ActionColumn actionColumn2 = new ActionColumn();
        actionColumnList.add(actionColumn2);
    }
}
