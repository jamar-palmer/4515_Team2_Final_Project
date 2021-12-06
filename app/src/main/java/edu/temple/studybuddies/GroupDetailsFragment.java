package edu.temple.studybuddies;

import android.app.DownloadManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupDetailsFragment extends Fragment {

    private GroupDetailsInterface parentActivity;
    protected static final String GROUP_ID = "groupId";
    private int memberCount;
    private String groupId;

    public GroupDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     */
    public static GroupDetailsFragment newInstance() {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(GROUP_ID);
            memberCount = 0;
        }
        parentActivity = (GroupDetailsInterface) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_details, container, false);
        TextView nameTextView = v.findViewById(R.id.groupDetailsNameVal);
        TextView ownerTextView = v.findViewById(R.id.groupDetailsOwnerVal);
        TextView MembersTextView = v.findViewById(R.id.groupDetailsMembersVal);
        Group displayGroup = new Group();
        displayGroup.setListeners(groupId, () ->{
            nameTextView.setText(displayGroup.name);
            final String owner = displayGroup.owner.substring(displayGroup.owner.indexOf('/'));
            StudyBuddies.db.collection(StudyBuddies.CollectionType.USERS).document(owner)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        ownerTextView.setText(documentSnapshot.getString("username"));
                    });
            StudyBuddies.db.collection(StudyBuddies.CollectionType.USERS)
                    .whereArrayContains("groups", "groups/" + groupId)
                    .get()
                    .addOnCompleteListener(task -> {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            memberCount++;
                        }
                        MembersTextView.setText(String.valueOf(memberCount));
                    });
        });

        Button broadcastButton = v.findViewById(R.id.broadcastGroupButton);
        Button leaveButton = v.findViewById(R.id.leaveGroupButton);
        Button joinButton = v.findViewById(R.id.groupDetailsJoinButton);

        broadcastButton.setOnClickListener(view -> {
            parentActivity.broadcastGroup();
        });
        leaveButton.setOnClickListener(view ->
                parentActivity.leaveGroup(groupId, () ->
                    configureUI(broadcastButton, leaveButton, joinButton)));
        joinButton.setOnClickListener(view ->
                parentActivity.joinGroup(groupId, () ->
                        configureUI(broadcastButton, leaveButton, joinButton)));
        configureUI(broadcastButton, leaveButton, joinButton);
        return v;
    }

    private void configureUI(Button broadcastButton, Button leaveButton, Button joinButton) {
        Log.d("DETAILS", "Group list: " + parentActivity.getActiveUser().groups);
        if(parentActivity.getActiveUser().groups == null || !parentActivity.getActiveUser().groups.contains(groupId)) {
            leaveButton.setVisibility(View.INVISIBLE);
            broadcastButton.setVisibility(View.INVISIBLE);
            joinButton.setVisibility(View.VISIBLE);
            return;
        }
        for(String id : parentActivity.getActiveUser().groups) {
            id = id.substring(id.lastIndexOf('/') + 1);
            Log.d("DETAILS", "groupId= " + groupId + "\tid= " + id);
            if(groupId.equals(id)) {
                Log.d("DETAILS", "Found group id in group list");
                leaveButton.setVisibility(View.VISIBLE);
                joinButton.setVisibility(View.INVISIBLE);
                StudyBuddies.db.collection(StudyBuddies.CollectionType.GROUPS).document(groupId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String tempOwner = documentSnapshot.getString("owner");
                            String owner = tempOwner.substring(tempOwner.lastIndexOf('/') + 1);
                            if (owner.equals(parentActivity.getActiveUser().id)) {
                                broadcastButton.setVisibility(View.VISIBLE);
                            } else {
                                broadcastButton.setVisibility(View.INVISIBLE);
                            }
                        });
            } else {
                leaveButton.setVisibility(View.INVISIBLE);
                broadcastButton.setVisibility(View.INVISIBLE);
                joinButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public String getGroupId() {
        return groupId;
    }

    public interface GroupDetailsInterface{
        User getActiveUser();
        void broadcastGroup();
        void leaveGroup(String groupId, User.RemoveGroupCallback callback);
        void joinGroup(String groupId, User.AddGroupCallback callback);
    }
}