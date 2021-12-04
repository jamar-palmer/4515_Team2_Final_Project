package edu.temple.studybuddies;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private DocumentReference doc;
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected List<String> groups;

    // Constructor creates a new User object
    // User must exist in the database already
    // otherwise use User.create()
    public User(String userId) {
        // remove path from ID
        if (userId.startsWith("users/")) {
            userId = userId.substring(groups.lastIndexOf('/') + 1);
        }
        // get the reference to the database document
        doc = StudyBuddies.db.collection("users").document(userId);
        // get the snapshot and fill data
        doc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                fillData(documentSnapshot);
                Log.d("USER", "Should see username: " + this.username);
            } else {
                Log.d("USER", "Unsuccessful");
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
        firstName = docSnap.getString("firstName");
        lastName = docSnap.getString("lastName");
        username = docSnap.getString("username");
        groups = (List<String>) docSnap.get("groups");
    }
}
