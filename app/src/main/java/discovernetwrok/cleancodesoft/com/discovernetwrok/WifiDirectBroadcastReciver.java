package discovernetwrok.cleancodesoft.com.discovernetwrok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;


public class WifiDirectBroadcastReciver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    public WifiDirectBroadcastReciver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainActivity mActivity) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            //indicates the state of Wi-Fi P2P connectivity has changed.
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, mActivity.connectionInfoListener);
            } else
                mActivity.connectionStaus.setText("Device disconnected");
            //  Toast.makeText(context, "WIFI_P2P_CONNECTION_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Indicates that the available peer list has changed.
            if (mManager != null) {
                mManager.requestPeers(mChannel, mActivity.peerListListener);
            }

            //   Toast.makeText(context, "WIFI_P2P_PEERS_CHANGED_ACTION", Toast.LENGTH_SHORT).show();

        } else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            //Indicates whether Wi-Fi P2P is enabled
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Toast.makeText(context, "WIFI_Is_ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "WIFI_Is_OFF", Toast.LENGTH_SHORT).show();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            //indicates this device's configuration details have changed.
            //   Toast.makeText(context, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
        }

    }
}
