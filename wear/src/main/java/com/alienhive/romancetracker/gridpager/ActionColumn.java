package com.alienhive.romancetracker.gridpager;

import com.alienhive.romancetracker.ActionPageFragment;
import com.alienhive.romancetracker.domain.RomanceAction;

public class ActionColumn {
    private ActionPageFragment fragment;

    public ActionColumn(RomanceAction action)
    {
        fragment = ActionPageFragment.newFragment(action.actionType, action.isBig);
    }

    public ActionPageFragment getFragment() {
        return fragment;
    }
}
