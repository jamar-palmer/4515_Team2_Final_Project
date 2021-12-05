package edu.temple.studybuddies;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements HomepageFragment.HomepageFragmentInterface, APIControllerFragment.ControllerInterface {

    static final String NEARBY_BROADCASTING = "broadcasting";
    static final String NEARBY_DISCOVERING = "discovering";
    static final String NEARBY_OFF = "off";

    FragmentManager fragmentManager;
    User activeUser;
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
                    pgService.startDiscovery(findViewById(R.id.groupRecyclerView));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
        if (bound)
            pgService.stopService();
            unbindService(mConnection);
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
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.mainContainer, new GroupListFragment())
                    .addToBackStack(null)
                    .commit();
            nearbyMode = NEARBY_DISCOVERING;
            bindService(new Intent(this,
                            ProximityGroupService.class),
                    mConnection, Context.BIND_AUTO_CREATE);
        })
        .setPositiveButton(R.string.join, (dialog, which) -> {
            // TODO: join group via "out of band"
        }).create().show();
    }
}