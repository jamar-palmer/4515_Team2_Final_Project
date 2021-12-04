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

    // return a Group object referencing the corresponding database document
    // this is expensive, so use a reference to this.group if you just need the ID
    public Group getGroup() {
        return new Group(this.group);
    }

}
