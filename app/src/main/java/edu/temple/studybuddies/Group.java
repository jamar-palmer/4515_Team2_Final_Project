package edu.temple.studybuddies;

import java.util.LinkedList;

public class Group {
    String groupId;
    String name;
    String ownerId;
    LinkedList<String> meetings;

    public Group(String id) {
        groupId = id;
        // contact the server to get data
    }
}
