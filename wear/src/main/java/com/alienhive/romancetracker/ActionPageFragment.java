package com.alienhive.romancetracker;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.ActionPage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActionPageFragment extends Fragment {

    public interface ActionPageFragmentListener {
        public void actionButtonClicked();
    }

    public static final String ACTION_TYPE_KEY = "ACTION_TYPE";
    public static final String ACTION_SIZE_KEY = "ACTION_SIZE";


    public static ActionPageFragment newFragment(String actionType, boolean isBig) {
        Bundle bundle = new Bundle();
        bundle.putString(ACTION_TYPE_KEY, actionType);
        bundle.putBoolean(ACTION_SIZE_KEY, isBig);
        ActionPageFragment fragment = new ActionPageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private String actionType;
    private boolean isBig = false;
    private ActionPage actionPage;
    private ActionPageFragmentListener listener;

    public void setListener(ActionPageFragmentListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        actionType = args.getString(ACTION_TYPE_KEY, "no action");
        isBig = args.getBoolean(ACTION_SIZE_KEY, false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.action_button_fragment, container, false);
        actionPage = (ActionPage) view.findViewById(R.id.actionPage_action);

        setActionText();
        setClickListener();
        return view;
    }

    private void setActionText() {
        if (isBig) {
            actionPage.setText("Big " + actionType);
        } else {
            actionPage.setText("Small " + actionType);
        }
    }

    private void setClickListener() {
        actionPage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preformAction();
            }
        });
    }

    private void preformAction()
    {
        if(listener != null)
        {
            listener.actionButtonClicked();
        }
    }
}
