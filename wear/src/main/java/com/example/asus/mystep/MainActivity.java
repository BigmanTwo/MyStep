package com.example.asus.mystep;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.gesture.Gesture;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.gesture.GestureType;
import com.mobvoi.android.gesture.MobvoiGestureClient;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;


public class MainActivity extends Activity implements MobvoiApiClient.ConnectionCallbacks,
        MobvoiApiClient.OnConnectionFailedListener, NodeApi.NodeListener, MessageApi.MessageListener {
    private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");
    private static final String TAG = "StepActivity";
    private static final String DEFAULT_NODE = "default_node";
    private int mSteps;
    private MobvoiApiClient mMobvoiApiClient;
    private FragmentManager manager;
    private MainFragment mainFragment;
    private UpFragment upFragment;
    private MobvoiGestureClient client;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler = new Handler();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        mainFragment = new MainFragment();
        mSteps = mainFragment.getmSteps();
        transaction.add(R.id.frame_select, mainFragment, "").commit();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mMobvoiApiClient.connect();
        client = MobvoiGestureClient.getInstance(GestureType.GROUP_TURN_WRIST);
        client.register(MainActivity.this, new MobvoiGestureClient.IGestureDetectedCallback() {
            @Override
            public void onGestureDetected(final int type) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction transaction = manager.beginTransaction();
                        String s = "";
                        if (type == GestureType.TYPE_TWICE_TURN_WRIST) {
                            //翻两次手腕
                        } else if (type == GestureType.TYPE_TURN_WRIST_UP) {
                            s = "向上翻页";

                            upFragment = new UpFragment();//向上翻页
                            transaction.replace(R.id.frame_select, upFragment);
                            transaction.addToBackStack(null).commit();
                        } else if (type == GestureType.TYPE_TURN_WRIST_DOWN) {
                            //向下翻页
                        } else {

                        }
                        Toast.makeText(getApplicationContext(), "onGestureDetected " + s, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        Wearable.NodeApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
        client.unregister(this);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
        sendMessagetoPhone();
    }

    @Override
    public void onConnectionSuspended(int cause) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Log.d(TAG, "onMessageReceived: " + event);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConncted:");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void sendMessagetoPhone() {
        byte[] data = String.valueOf(mSteps).getBytes();
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, DEFAULT_NODE, "Steps", data).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        } else {
                            Log.d(TAG, "Success");
                        }
                    }
                }
        );
    }

}
