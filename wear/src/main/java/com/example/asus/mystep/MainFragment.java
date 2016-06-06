package com.example.asus.mystep;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.Wearable;

public class MainFragment extends Fragment {
    private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");
    private static final String DEFAULT_NODE = "default_node";
    private static final String TAG = "StepActivity";

    private ContentResolver mResolver;
    private int mSteps;
    private  ContentObserver mObserver;
    private TextView mStepTv;
    private RoundProgressBar roundProgressBar;



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            roundProgressBar.setProgress(mSteps);
            mStepTv.setText(getString(R.string.step_count));

        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_main_fragment,null);
        roundProgressBar=(RoundProgressBar)view.findViewById(R.id.progress);
        mStepTv = (TextView) view.findViewById(R.id.step_tv);
        init();
        return view;
    }
    private void init() {


        mResolver = getActivity().getContentResolver();
        mObserver = new ContentObserver(mHandler) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mSteps = fetchSteps();

                Message.obtain(mHandler, mSteps).sendToTarget();
            }
        };

        mSteps = fetchSteps();
        roundProgressBar.setProgress(mSteps);
        mStepTv.setText(getString(R.string.step_count));
        registerContentObserver();
    }

    private int fetchSteps() {
        int steps = 0;
        int distance=0;
        Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    steps = cursor.getInt(0);
                    //距离的单位是米
                    distance=cursor.getInt(1);
                }
            } finally {
                cursor.close();
            }
        }
        return steps;
    }
    private void unregisterContentObserver() {
        mResolver.unregisterContentObserver(mObserver);
    }
    private void registerContentObserver() {
        mResolver.registerContentObserver(STEP_URI, true, mObserver);
    }


    public int getmSteps(){
        return mSteps;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterContentObserver();
    }

}
