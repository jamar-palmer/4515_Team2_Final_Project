package edu.temple.studybuddies;

import java.util.LinkedList;

public class Group {
    String id;
    String name;
    String owner;
    LinkedList<String> meetings;

    public Group(String groupId) {
        id = groupId;
        // contact the server to get data
    }
}
