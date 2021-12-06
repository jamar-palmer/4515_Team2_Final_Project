package edu.temple.studybuddies;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class StudyBuddies {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    /** Generic function to add a document to the specified collection on the database
     * @param data {@code Map} containing fields to be added to the document
     * @param collection Name of the database collection to add the document to
     *                   (choose from {@codeStudyBuddies.CollectionType})
     * @param addDocumentCallback A callback function that takes a {@code DocumentReference}
     *                 referencing the created document
     */
    public static void addDocument(Map<String,Object> data, String collection, AddDocumentCallback addDocumentCallback) {
        DocumentReference ref = StudyBuddies.db.collection(collection).document();
        ref.set(data)
                .addOnSuccessListener(unused -> {
                    Log.d(collection, "Write successful");
                    addDocumentCallback.onDocumentCreate(ref);
                })
                .addOnFailureListener(e -> {
                    Log.d(collection, "Write failed: " + e);
                });
    }

    // use these to refer to a collection name
    public class CollectionType {
        public static final String USERS = "users";
        public static final String GROUPS = "groups";
        public static final String MEETINGS = "meetings";
    }

    public interface AddDocumentCallback {
        void onDocumentCreate(DocumentReference ref);
    }
}
