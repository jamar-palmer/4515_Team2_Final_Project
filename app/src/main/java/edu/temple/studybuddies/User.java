package edu.temple.studybuddies;

import java.util.LinkedList;

public class User {
    String id;
    String firstName;
    String lastName;
    LinkedList<String> groups;

    public User(String userId) {
        id = userId;
        // contact server to get data
    }
}
