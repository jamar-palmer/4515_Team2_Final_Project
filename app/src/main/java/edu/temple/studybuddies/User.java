package edu.temple.studybuddies;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
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
    // if needed in group and meeting functionality, please implement it like in this class
    private Map<String,Object> data;

    protected DocumentReference doc;
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String username;
    private String password;
    protected List<String> groups;

    /** Constructor creates an empty group
     *  Must be initialized with a call to {@code setListeners}
     */
    public User(){

    }
    /** Constructor creates a new Group object
     * Group must exist in the database already
     */
    public User(String userId) {
        this(userId, () -> {});
    }
    /** Constructor creates a new Group object with a callback
     * Group must exist in the database already
     */
    public User(String userId, NewUserCallback callback) {
        // remove path from ID
        if (userId.startsWith("users/")) {
            userId = userId.substring(userId.lastIndexOf('/') + 1);
        }
        setListeners(userId, callback);
    }

    public void setListeners(String userId, NewUserCallback callback) {
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

    // helper method for constructor
    private void fillData(DocumentSnapshot docSnap) {
        id = docSnap.getId();
        firstName = docSnap.getString(FIRSTNAME);
        lastName = docSnap.getString(LASTNAME);
        username = docSnap.getString(USERNAME);
        password = docSnap.getString(PASSWORD);
        groups = (List<String>) docSnap.get(GROUPS);
        if(data.isEmpty()) {
            data.put(FIRSTNAME, firstName);
            data.put(LASTNAME, lastName);
            data.put(USERNAME, username);
            data.put(PASSWORD, password);
            data.put(GROUPS, groups);
        }
    }

    public void addGroup(String groupId, AddGroupCallback callback) {
        if(groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(groupId);
        data.put(GROUPS, groups);
        doc.set(data).addOnSuccessListener(unused -> {
                callback.onGroupAdded();
                Log.d("ADD GROUP", "Added group with ID: " + groupId);
            });
    }

    public void removeGroup(String groupId, RemoveGroupCallback callback) {
        if(groups == null) {
            return;
        }
        groups.remove(groupId);
        data.put(GROUPS, groups);
        doc.set(data).addOnSuccessListener(unused -> {
            callback.onGroupRemoved();
            Log.d("RM GROUP", "Removed group with ID: " + groupId);
        });
    }

    public interface NewUserCallback {
        void onNewUser();
    }
    public interface AddGroupCallback {
        void onGroupAdded();
    }
    public interface RemoveGroupCallback {
        void onGroupRemoved();
    }
}
