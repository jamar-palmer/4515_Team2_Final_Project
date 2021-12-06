package edu.temple.studybuddies;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements HomepageFragment.HomepageFragmentInterface, APIControllerFragment.ControllerInterface, GroupDetailsFragment.GroupDetailsInterface {

    static final String NEARBY_ADVERTISING = "broadcasting";
    static final String NEARBY_DISCOVERING = "discovering";
    static final String NEARBY_OFF = "off";

    private RecyclerView recyclerView;
    private ArrayList<Group> groupList;
    private GroupAdapter groupAdapter;

    FragmentManager fragmentManager;
    public User activeUser;
    boolean bound;
    String nearbyMode;
    ProximityGroupService pgService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bound = true;
            pgService = ((ProximityGroupService.ProximityGroupBinder) service).getService();
            pgService.setUserId(activeUser.id);

            if (nearbyMode.equals(NEARBY_DISCOVERING)) {
                try {
                    Log.d("SERVICE", "Calling start discovery");
                    pgService.startDiscovery(findViewById(R.id.groupRecyclerView));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (nearbyMode.equals(NEARBY_ADVERTISING)) {
                GroupDetailsFragment currentGroupFragment =
                        (GroupDetailsFragment) fragmentManager
                                .findFragmentByTag("currentGroupFragment");
                try {
                    pgService.startAdvertising(currentGroupFragment.getGroupId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    isGranted -> {
                        boolean allGranted = true;
                        for (String k : isGranted.keySet()) {
                            if(allGranted && !isGranted.get(k)) {
                                allGranted = false;
                            }
                        }
                        if(allGranted){
                            // maybe do something here
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent callingIntent = getIntent();
        if (!callingIntent.getExtras().isEmpty()) {
            String activeUserId = callingIntent.getExtras().getString("ACTIVE_USER_ID");
            activeUser = new User(activeUserId, () -> {
                Log.d("MAIN", "Active user: " + activeUser.username);
            });
        }
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.navContainer, new APIControllerFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.mainContainer, new HomepageFragment()).commit();

    }

    @Override
    protected void onStop() {
        if (bound) {
            pgService.stopService();
            unbindService(mConnection);
        }
        super.onStop();
    }

    public void logout(View view) {
        Intent intent = new Intent(this, LoginAndRegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void createGroup() {
        Intent intent = new Intent(this, GroupCreatorActivity.class);
        startActivity(intent);
    }

    @Override
    public void createMeeting() {
        Intent intent = new Intent(this, MeetingCreatorActivity.class);
        startActivity(intent);
    }

    @Override
    public void launchJoinGroupDialogue() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle(getString(R.string.join_group_title))
                .setMessage(getString(R.string.join_group_message))
                .setView(inflater.inflate(R.layout.join_group_dialogue, null))
        .setNegativeButton(R.string.cancel, (dialog, which) -> {
            // nothing
        })
        .setNeutralButton(R.string.nearby, (dialog, which) -> {
            if (!checkAllPermissions()) {
                getUserPermission();
            }
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.mainContainer, new GroupListFragment())
                    .addToBackStack(null)
                    .commit();
            nearbyMode = NEARBY_DISCOVERING;
            Intent intent = new Intent(getApplicationContext(), ProximityGroupService.class);
            startService(intent);
            bindService(intent, mConnection, BIND_AUTO_CREATE);
        })
        .setPositiveButton(R.string.join, (dialog, which) -> {
            // TODO: join group via "out of band"
        }).create().show();
    }

    @Override
    public void viewGroups() {
        if (activeUser.groups != null && activeUser.groups.size() > 0) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.mainContainer, new GroupListFragment())
                    .addToBackStack(null)
                    .commit();
            fragmentManager.executePendingTransactions();
            groupList = new ArrayList<>();
            groupAdapter = new GroupAdapter(groupList);
            recyclerView = findViewById(R.id.groupRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(groupAdapter);
            for (String groupId : activeUser.groups) {
                groupList.add(new Group(groupId, () -> {
                    groupAdapter.notifyDataSetChanged();
                }));
                groupAdapter.notifyItemInserted(groupList.size() - 1);
            }
        } else {
            Toast.makeText(this, "You are not currently in a group", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public User getActiveUser() {
        return activeUser;
    }

    @Override
    public void broadcastGroup() {
        if (!checkAllPermissions()) {
            getUserPermission();
        }
        nearbyMode = NEARBY_ADVERTISING;
        Intent intent = new Intent(getApplicationContext(), ProximityGroupService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void leaveGroup(String groupId, User.RemoveGroupCallback callback) {
        activeUser.removeGroup(groupId, callback);
    }

    @Override
    public void joinGroup(String groupId, User.AddGroupCallback callback) {
        activeUser.addGroup(groupId, callback);
    }

    @Override
    public void home() {
        fragmentManager
                .beginTransaction()
                .replace(R.id.mainContainer, new HomepageFragment())
                .commit();
    }

    private boolean checkAllPermissions() {
        return ContextCompat.checkSelfPermission(this, "ALL_PERMISSIONS") == PackageManager.PERMISSION_GRANTED;
    }

    private void getUserPermission() {
        requestPermissionLauncher.launch(new String[] {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        });
    }
}