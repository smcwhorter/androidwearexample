package com.alienhive.romancetracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.alienhive.romancetracker.domain.ActionPageRow;

import java.util.ArrayList;

public class ActionPickerGridPagerAdapter extends FragmentGridPagerAdapter {
    private Context context;
    ArrayList<ActionPageRow> actionPageList;

    public ActionPickerGridPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        createActionPages();
    }

    private void createActionPages() {
        actionPageList = new ArrayList<>(2);
        ActionPageRow actionRow1 = new ActionPageRow("Hugs", "Track big or small hugs.");
        ActionPageRow actionRow2 = new ActionPageRow("Kisses", "Track big or small kisses.");
        actionPageList.add(actionRow1);
        actionPageList.add(actionRow2);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        return actionPageList.get(row).getFragment(col);

    }

    @Override
    public int getRowCount() {
        if (actionPageList != null)
            return actionPageList.size();
        else
            return 0;
    }

    @Override
    public int getColumnCount(int i) {
        if (actionPageList != null)
            return actionPageList.get(i).getColumnCount();
        else
            return 0;
    }
}
