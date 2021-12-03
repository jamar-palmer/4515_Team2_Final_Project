package edu.temple.studybuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity implements HomepageFragment.HomepageFragmentInterface {

    FragmentManager fragmentManager;
    User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent callingIntent = getIntent();
        if (!callingIntent.getExtras().isEmpty()) {
            String activeUserId = callingIntent.getExtras().getString("ACTIVE_USER_ID");
            activeUser = new User(activeUserId);
            Log.d("MAIN", "Active user: " + activeUser.username);
        }
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.navContainer, new APIControllerFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.mainContainer, new HomepageFragment()).commit();

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
}