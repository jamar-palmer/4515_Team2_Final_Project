package edu.temple.studybuddies;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    // use this when you want to add a User document to the database
    // creates document in database and returns a User object containing the data
    public static User create(String firstName, String lastName, String username, String password) throws InterruptedException {
        DocumentReference ref = StudyBuddies.db.collection("users").document();
        Map<String, Object> nUser = new HashMap<>();
        nUser.put("firstName",firstName);
        nUser.put("lastName",lastName);
        nUser.put("username",username);
        nUser.put("password",password);
        ref.set(nUser)
                .addOnSuccessListener(unused -> {
                    Log.d("User", "Write successful");
                })
                .addOnFailureListener(e -> {
                    Log.d("User", "Write failed: " + e);
                });

        // remember, since we call the constructor
        // the data is updated in the app when it changes on the database
        // cuts down on code repetition but
        // the downside is that we have to contact the database to write
        // then here we read from it again, but I'm not sure there's really a better way
        return new User(ref.getId());
    }
}
