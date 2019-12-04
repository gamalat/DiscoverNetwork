package discovernetwrok.cleancodesoft.com.discovernetwrok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WifiManager wifiManager;
    WifiP2pManager.Channel channel;
    WifiP2pManager mManager;
    Button btnOnOff, btnDiscover;
    ListView wifiListView;
    TextView connectionStaus;
    // Button  btnSend;
    //    TextView read_msg_box;
    //   EditText writeMessage;
    BroadcastReceiver mRecevier;
    IntentFilter intentFilter = new IntentFilter();
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] devicesNameArray;
    WifiP2pDevice[] deviceArray;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initalWork();//method have all inital work that used in on create
        exqListener();//hava all actions listener

    }

    private void exqListener() {
        //btn to run the wifi on and off
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(false);
                    btnOnOff.setText("ON");

                } else
                    wifiManager.setWifiEnabled(true);
                btnOnOff.setText("OFF");
            }
        });
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStaus.setText("discovey started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectionStaus.setText("discovey failed");
                    }
                });

            }
        });
        wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiP2pDevice device = deviceArray[position];
                WifiP2pConfig config = new WifiP2pConfig();
                //             WifiConfiguration configuration=new WifiConfiguration();
                config.deviceAddress = device.deviceAddress;
                mManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "connect to" + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                    }
                });
            }
        });

    }

    private void initalWork() {
        btnOnOff = (Button) findViewById(R.id.onOff);
        btnDiscover = (Button) findViewById(R.id.discover);
        //  btnSend = (Button) findViewById(R.id.sendButton);
        wifiListView = (ListView) findViewById(R.id.peerListView);
        //  read_msg_box = (TextView) findViewById(R.id.readMsg);
        connectionStaus = (TextView) findViewById(R.id.connectionStatus);
        //  writeMessage = (EditText) findViewById(R.id.writeMsg);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = mManager.initialize(this, getMainLooper(), null);
        mRecevier = new WifiDirectBroadcastReciver(mManager, channel, this);
        // registerReceiver(mRecevier, intentFilter);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                // If an AdapterView is backed by this data, notify it
                // of the change. For instance, if you have a ListView of
                // available peers, trigger an update.
                devicesNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
                index = 0;
                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    devicesNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, devicesNameArray);
                wifiListView.setAdapter(adapter);
            }
            if (peers.size() == 0) {
                Toast.makeText(MainActivity.this, "no devices is foun", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    };
    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionStaus.setText("Host");

            } else if (wifiP2pInfo.groupFormed) {
                connectionStaus.setText("client");
            }
        }
    };

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mRecevier, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mRecevier);
    }
}
