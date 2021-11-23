package edu.temple.studybuddies;

import java.util.Date;

public class Meeting {
    String id;
    Date startTime;
    Date endTime;
    String groupId;

    public Meeting(String meetingId) {
        id = meetingId;
        // contact server to get data
    }
}
