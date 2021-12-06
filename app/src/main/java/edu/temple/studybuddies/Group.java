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


    /** Constructor creates an empty group
     *  Must be initialized with a call to {@code setListeners}
     *
     */
    public Group() {

    }
    /** Constructor creates a new Group object
     * Group must exist in the database already
     */
    public Group(String groupId) {
        this(groupId, () -> {});
    }
    /** Constructor creates a new Group object with a callback
     * Group must exist in the database already
     */
    public Group(String groupId, NewGroupCallback callback) {
        //  remove path from ID
        if(groupId.startsWith("groups")) {
            groupId = groupId.substring(groupId.lastIndexOf('/') + 1);
        }
        setListeners(groupId, callback);
    }

    public void setListeners(String groupId, NewGroupCallback callback) {
        // get the reference to the database document
        doc = StudyBuddies.db.collection("groups").document(groupId);
        // get the snapshot and fill data
        doc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                fillData(documentSnapshot);
                callback.onNewGroup();
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

    public interface NewGroupCallback {
        void onNewGroup();
    }
}
