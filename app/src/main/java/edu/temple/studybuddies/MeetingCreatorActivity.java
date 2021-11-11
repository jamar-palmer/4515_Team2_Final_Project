package edu.temple.studybuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class MeetingCreatorActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_creator);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.meetingContainer, new MeetingFragment()).commit();
    }
}