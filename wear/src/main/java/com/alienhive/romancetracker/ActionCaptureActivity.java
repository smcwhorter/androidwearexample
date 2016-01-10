package com.alienhive.romancetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;

public class ActionCaptureActivity extends Activity {

    public static Intent buildIntent(Context context)
    {
        return new Intent(context, ActionCaptureActivity.class);
    }

    private GridViewPager gridViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_capture);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                gridViewPager = (GridViewPager) stub.findViewById(R.id.pager);
                setupGridViewPager();
            }
        });
    }

    private void setupGridViewPager()
    {
        ActionPickerGridPagerAdapter adapter = new ActionPickerGridPagerAdapter(this, getFragmentManager());
        gridViewPager.setAdapter(adapter);
    }
}
