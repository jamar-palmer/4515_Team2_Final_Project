package edu.temple.studybuddies;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group {
    private DocumentReference doc;
    protected String id;
    protected String name;
    protected String owner;
    protected List<String> meetings;

    // Constructor creates a new Group object
    // Group must exist in the database already
    // otherwise use Group.create()
    public Group(String groupId) {
        //  remove path from ID
        if(groupId.startsWith("groups")) {
            groupId = groupId.substring(meetings.lastIndexOf('/') + 1);
        }
        // get the reference to the database document
        doc = StudyBuddies.db.collection("groups").document(groupId);
        // get the snapshot and fill data
        doc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                fillData(documentSnapshot);
            } else {
                return;
            }
        });
        // listen for changes to document and fill data when a change is detected
        doc.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                fillData(value);
            }
        });
    }

    // helper method for constructor
    private void fillData(DocumentSnapshot docSnap) {
        id = docSnap.getId();
        name = docSnap.getString("name");
        owner = docSnap.getString("owner");
        meetings = (List<String>) docSnap.get("meetings");
    }

    // use this when you want to add a Group document to the database
    // creates document in database and returns a Group object containing the data
    public static Group create(String group, String owner) {
        Map<String, Object> nGroup = new HashMap<>();
        nGroup.put("name", group);
        if (!owner.startsWith("users/")) {
            owner = "users/" + owner;
        }
        nGroup.put("owner", owner);
        DocumentReference ref = StudyBuddies.db.collection("groups").document();
                ref.set(nGroup)
                .addOnSuccessListener(unused -> {
                    Log.d("Group", "Write successful");
                })
                .addOnFailureListener(e -> {
                    Log.d("Group", "Write failed: " + e);
                });
        // remember, since we call the constructor
        // the data is updated in the app when it changes on the database
        // cuts down on code repetition but
        // the downside is that we have to contact the database to write
        // then here we read from it again, but I'm not sure there's really a better way
        return new Group(ref.getId());
    }

    // create a new meeting on the database
    // then use the returned meeting object
    // to get the ID and add it to the List<> of meetings
    // the Meeting object is garbage collected and we keep the String
    public void addMeeting(Date startTime, Date endTime, Uri url) {
        Meeting nMeeting = Meeting.create(startTime, endTime, url, this.id);
        meetings.add(nMeeting.id);
    }

    // return a User object referencing the corresponding database document
    // this is expensive, so use a reference to this.owner if you just need the ID
    public User getOwner() {
        return new User(this.owner);
    }
}
