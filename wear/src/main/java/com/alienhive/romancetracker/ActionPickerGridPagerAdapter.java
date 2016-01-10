package com.alienhive.romancetracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.alienhive.romancetracker.domain.ActionPageRow;

import java.util.ArrayList;

/**
 * Created by sm15461 on 1/10/16.
 */
public class ActionPickerGridPagerAdapter extends FragmentGridPagerAdapter
{
    private Context context;
    private FragmentManager fm;
    private ArrayList<ActionPageRow> actionPageList;

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
    }

    @Override
    public Fragment getFragment(int row, int col) {
        return actionPageList.get(row).getFragment();
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
        return 1;
    }
}
