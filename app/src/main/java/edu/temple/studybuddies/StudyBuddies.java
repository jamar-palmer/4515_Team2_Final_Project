package edu.temple.studybuddies;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class StudyBuddies {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void addDocument(Map<String,Object> data, String collection, Callback callback) {
        DocumentReference ref = StudyBuddies.db.collection(collection).document();
        ref.set(data)
                .addOnSuccessListener(unused -> {
                    Log.d(collection, "Write successful");
                    callback.onDocumentCreate(ref);
                })
                .addOnFailureListener(e -> {
                    Log.d(collection, "Write failed: " + e);
                });
    }

    public class CollectionType {
        public static final String USERS = "users";
        public static final String GROUPS = "groups";
        public static final String MEETINGS = "meetings";
    }

    public interface Callback {
        void onDocumentCreate(DocumentReference ref);
    }
}
