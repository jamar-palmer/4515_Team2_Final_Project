package edu.temple.studybuddies;

import android.telephony.mbms.GroupCall;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    final static String FIRSTNAME = "firstName";
    final static String LASTNAME = "lastName";
    final static String USERNAME = "username";
    final static String PASSWORD = "password";
    final static String GROUPS = "groups";

    // used to edit data on the server using set method on the DocumentReference
    private Map<String,Object> data;

    protected DocumentReference doc;
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected List<String> groups;

    // Constructor creates a new User object
    // User must exist in the database already
    public User(String userId, UserCallback callback) {
        // remove path from ID
        if (userId.startsWith("users/")) {
            userId = userId.substring(userId.lastIndexOf('/') + 1);
        }
        // instantiate Map
        data  = new HashMap<>();
        // get the reference to the database document
        doc = StudyBuddies.db.collection("users").document(userId);
        // get the snapshot and fill data
        doc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                fillData(documentSnapshot);
                Log.d("USER", "Should see username: " + this.username);
                callback.onNewUser();
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

    public User(String userId) {
        this(userId, () -> {});
    }

    // helper method for constructor
    private void fillData(DocumentSnapshot docSnap) {
        id = docSnap.getId();
        firstName = docSnap.getString(FIRSTNAME);
        lastName = docSnap.getString(LASTNAME);
        username = docSnap.getString(USERNAME);
        groups = (List<String>) docSnap.get(GROUPS);
        if(data.isEmpty()) {
            data.put(FIRSTNAME, firstName);
            data.put(LASTNAME, lastName);
            data.put(USERNAME, username);
            data.put(GROUPS, groups);
        }
    }

    public void addGroup(String groupId) {
        groups.add(groupId);
        doc.set(data).addOnSuccessListener(unused ->
                Log.d("ADD GROUP","Added group with ID: " + groupId));
    }

    public interface UserCallback {
        void onNewUser();
    }
}
