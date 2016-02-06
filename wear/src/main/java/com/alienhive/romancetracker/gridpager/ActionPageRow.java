package com.alienhive.romancetracker.gridpager;

import android.app.Fragment;

import com.alienhive.romancetracker.ActionTypeCardFragment;
import com.alienhive.romancetracker.domain.RomanceAction;

import java.util.ArrayList;

public class ActionPageRow {

    //Private fields
    private RomanceAction action;
    private Fragment actionTypeFragment;
    private ArrayList<ActionColumn> actionColumnList;

    /**
     * Constructor
    */
    public ActionPageRow(RomanceAction action)
    {
        this.action = action;
        setupActionTypeColumn(action);
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

    private void setupActionTypeColumn(RomanceAction action) {
        actionTypeFragment = ActionTypeCardFragment.newInstance(action.title, action.description);
    }

    private void setupActionColumns() {
        actionColumnList = new ArrayList<>(2);

        ActionColumn actionColumn1 = new ActionColumn(RomanceAction.createBigAction(this.action));
        actionColumnList.add(actionColumn1);
        ActionColumn actionColumn2 = new ActionColumn(this.action);
        actionColumnList.add(actionColumn2);
    }
}
