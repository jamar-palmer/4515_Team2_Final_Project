package edu.temple.studybuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class GroupCreatorActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creator);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.groupContainer, new GroupFragment()).commit();
    }
}