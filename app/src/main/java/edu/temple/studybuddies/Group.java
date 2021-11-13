package edu.temple.studybuddies;

import java.util.LinkedList;

public class Group {
    String id;
    String name;
    String owner;
    LinkedList<String> meetings;

    public Group(String groupId) {
        id = groupId;
        // replace this after contacting server
        name = "default group name";
        // contact the server to get data
    }
}
