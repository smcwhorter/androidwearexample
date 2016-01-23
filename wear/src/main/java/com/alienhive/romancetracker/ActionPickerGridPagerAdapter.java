package com.alienhive.romancetracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.alienhive.romancetracker.domain.ActionColumn;
import com.alienhive.romancetracker.domain.ActionPageRow;

import java.util.ArrayList;

public class ActionPickerGridPagerAdapter extends FragmentGridPagerAdapter
{
    private Context context;
    private FragmentManager fm;
    private ArrayList<ActionPageRow> actionPageList;
    private ArrayList<ActionColumn> actionColumnList;

    public ActionPickerGridPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        this.fm = fm;
        createActionPages();
    }

    private void createActionPages()
    {
        actionPageList = new ArrayList<>(2);
        ActionPageRow row0 = new ActionPageRow("Hugs", "Track big or small hugs.");
        ActionPageRow row1 = new ActionPageRow("Kisses", "Track big or small kisses.");
        actionPageList.add(row0);
        actionPageList.add(row1);

        actionColumnList = new ArrayList<>(2);
        ActionColumn actionColumn1 = new ActionColumn();
        actionColumnList.add(actionColumn1);
        ActionColumn actionColumn2 = new ActionColumn();
        actionColumnList.add(actionColumn2);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        if(((row == 0) || (row == 1)) && (col == 0)) {
            return actionPageList.get(row).getFragment();
        }
        else if(((row == 0) || (row == 1)) && (col == 1)) {
            return actionColumnList.get(row).getFragment();
        }
        else
        {
            return null;
        }
    }

    @Override
    public int getRowCount() {
        if(actionPageList != null)
            return actionPageList.size();
        else
            return 0;
    }

    @Override
    public int getColumnCount(int i) {
            return 2;
    }
}
