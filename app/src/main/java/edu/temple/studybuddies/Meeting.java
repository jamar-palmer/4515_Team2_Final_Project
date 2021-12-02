package edu.temple.studybuddies;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Meeting {
    private DocumentReference doc;
    protected String id;
    protected Date startTime;
    protected Date endTime;
    protected Uri url;
    protected String group;

    // Constructor creates a new Meeting object
    // Meeting must exist in the database already
    // otherwise use Meeting.create()
    public Meeting(String meetingId) {
        // remove path from ID
        if (meetingId.startsWith("meetings/")) {
            meetingId = meetingId.substring(meetingId.lastIndexOf('/') + 1);
        }
        // get the reference to the database document
        doc = StudyBuddies.db.collection("meetings").document(meetingId);
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
        startTime = docSnap.getDate("startTime");
        endTime = docSnap.getDate("endTime");
        group = docSnap.getString("group");
        url = Uri.parse(docSnap.getString("url"));
    }

    // use this when you want to add a Meeting document to the database
    // creates document in database and returns a Meeting object containing the data
    public static Meeting create(Date startTime, Date endTime, Uri url, String group) {
        Map<String, Object> nMeeting = new HashMap<>();
        nMeeting.put("startTime", startTime);
        nMeeting.put("endTime", endTime);
        // make sure group reference has the full path
        if (!group.startsWith("/groups")) {
            group = "/groups" + group;
        }
        nMeeting.put("group", group);
        nMeeting.put("url", url.getPath());
        DocumentReference ref = StudyBuddies.db.collection("meetings").document();
        ref.set(nMeeting)
                .addOnSuccessListener(unused -> {
                    Log.d("Meeting", "Write successful");
                })
                .addOnFailureListener(e -> {
                    Log.d("Meeting", "Write failed: " + e);
                });
        // remember, since we call the constructor
        // the data is updated in the app when it changes on the database
        // cuts down on code repetition but
        // the downside is that we have to contact the database to write
        // then here we read from it again, but I'm not sure there's really a better way
        return new Meeting(ref.getId());
    }

    // return a Group object referencing the corresponding database document
    // this is expensive, so use a reference to this.group if you just need the ID
    public Group getGroup() {
        return new Group(this.group);
    }

    // example of usage case for getGroup()
    // kind of ugly and I'm sure there's a better way
    // we should probably try to avoid doing things like this
    // leaving it here cause if we need it, it works fine
    // maybe... all of this is actually untested at the time of this comment
    public User getMeetingGroupOwner() {
        return new User(this.getGroup().owner);
    }
}
