package edu.temple.studybuddies;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProximityGroupService extends Service {

    static final String SERVICE_ID = "edu.temple.studybuddies.SERVICE_ID";
    static final int ONGOING_NOTIFICATION_ID = 69;
    static final String CHANNEL_ID = "edu.temple.studybuddies.CHANNEL_ID";
    static final String CHANNEL_NAME = "edu.temple.studybuddies.CHANNEL_NAME";

    private Notification notification;
    private String userId;
    private String groupId;
    private ArrayList<String> connections;
    private ArrayList<Group> groupList;
    private GroupAdapter groupAdapter;

    public String selectedItem;

    private final IBinder binder = new ProximityGroupBinder();

    public class ProximityGroupBinder extends Binder {
        ProximityGroupService getService() {
            return ProximityGroupService.this;
        }
    }

    public ProximityGroupService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERVICE", "OnStartCommand called");
        connections = new ArrayList<>();
        groupList = new ArrayList<>();
        makeNotification();
        startForeground(ONGOING_NOTIFICATION_ID, notification);
        return START_NOT_STICKY;
    }

    // CALL getService ON THE binder OBJECT TO GAIN ACCESS TO THESE PUBLIC METHODS ***

    public void setUserId (String uid) {
        userId = uid;
    }

    public void startAdvertising(String groupId) throws Exception {
        if(userId == null || userId.equals("")) {
            throw new Exception("Must first call setUserId in calling Activity");
        }
        this.groupId = groupId;
        startAdvertisingLogic();
    }

    public void startDiscovery(RecyclerView view) throws Exception {
        if(userId == null || userId.equals("")) {
            throw new Exception("Must first call setUserId in calling Activity");
        }

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(groupAdapter);
        startDiscoveryLogic();
    }

    public void stopService() {
        this.stopSelf();
    }

    // END SECTION CONTAINING PUBLIC METHODS ***


    @Override
    public void onDestroy() {
        Nearby.getConnectionsClient(this).stopAdvertising();
        Nearby.getConnectionsClient(this).stopDiscovery();
        super.onDestroy();
    }

    private void makeNotification() {
        makeChannel();
        Intent intent = new Intent(this, ProximityGroupService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification =
                    new Notification.Builder(this, CHANNEL_ID)
                            .setContentTitle(getText(R.string.notification_title))
                            .setContentText(getText(R.string.notification_message))
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentIntent(pendingIntent)
                            .setTicker(getText(R.string.notification_message))
                            .build();
        }
    }

    private void makeChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    private void startAdvertisingLogic() {
        AdvertisingOptions advertisingOptions =
                new AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(this)
                .startAdvertising(
                        userId, SERVICE_ID, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                        });
    }
    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {

        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            // immediately accepts connection
            Nearby.getConnectionsClient(getApplicationContext()).acceptConnection(endpointId, new PayloadCallback() {
                @Override
                public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                    connections.add(endpointId);
                    if(groupId == null || groupId.equals("")) {
                        String data = new String(payload.asBytes());
                        Group group = new Group(data);
                        groupList.add(group);
                        groupAdapter.notifyItemInserted(groupList.indexOf(group));
                    }
                }

                @Override
                public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {

                }
            });
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution connectionResolution) {
            if(groupId != null || !groupId.equals("")) {
                Payload toSend = Payload.fromBytes(groupId.getBytes());
                Nearby.getConnectionsClient(getApplicationContext()).sendPayload(endpointId, toSend);
            }
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            int pos = connections.indexOf(endpointId);
            if(groupId == null || groupId.equals("")) {
                groupList.remove(pos);
                groupAdapter.notifyItemRemoved(pos);
            }
            connections.remove(pos);
        }
    };

    private void startDiscoveryLogic() {
        DiscoveryOptions discoveryOptions =
                new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build();
        Nearby.getConnectionsClient(this)
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            Toast.makeText(this, "Now discovering", Toast.LENGTH_LONG).show();
                        })
                .addOnFailureListener(
                        (Exception e) -> {

                        });
    }

    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                    Nearby.getConnectionsClient(getApplicationContext())
                            .requestConnection(userId, endpointId, connectionLifecycleCallback)
                            .addOnSuccessListener(
                                    (Void unused) -> {

                                    })
                            .addOnFailureListener(
                                    (Exception e) -> {

                                    });
                }

                @Override
                public void onEndpointLost(@NonNull String s) {

                }
            };
}