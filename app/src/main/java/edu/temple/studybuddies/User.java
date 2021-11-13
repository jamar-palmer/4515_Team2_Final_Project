package edu.temple.studybuddies;

import java.util.LinkedList;

public class User {
    String userId;
    String firstName;
    String lastName;
    LinkedList<String> groups;

    public User(String id) {
        userId = id;
        // contact server to get data
    }
}
