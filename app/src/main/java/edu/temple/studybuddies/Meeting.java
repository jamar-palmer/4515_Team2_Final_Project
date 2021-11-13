package edu.temple.studybuddies;

import java.util.Date;

public class Meeting {
    String meetingId;
    Date startTime;
    Date endTime;
    String groupId;

    public Meeting(String id) {
        meetingId = id;
        // contact server to get data
    }
}
