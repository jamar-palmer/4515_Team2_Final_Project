package edu.temple.studybuddies;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MeetingCreatorActivity extends AppCompatActivity implements MeetingDetailsFragment.MeetingDetailsFragmentInterface, MeetingFragment.MeetingFragmentInterface {

    FragmentManager fragmentManager;
    int meetingYear;
    int meetingMonth;
    int meetingDay;
    Integer startTimeHour = null;
    Integer startTimeMinute = null;
    Integer endTimeHour = null;
    Integer endTimeMinute = null;
    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        @Override
        public void onZoomSDKLoginResult(long result) {
            if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(MeetingCreatorActivity.this);
            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) {}@Override
        public void onZoomIdentityExpired() {}@Override
        public void onZoomAuthIdentityExpired() {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_creator);

        Intent intent = getIntent();
        int director = intent.getIntExtra("fragment", -1);
        fragmentManager = getSupportFragmentManager();
        if(director == 0){
            fragmentManager.beginTransaction().replace(R.id.meetingContainer, new MeetingFragment()).commit();
        }else{
            fragmentManager.beginTransaction().replace(R.id.meetingContainer, new MeetingDetailsFragment()).commit();
        }

        initializeZoom(this);
    }

    private void initializeZoom(Context context){
        ZoomSDK sdk = ZoomSDK.getInstance();
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "LRll49lGJE9egSC1zjD1RBt4cMdhfZ3pJ5ZE";
        params.appSecret = "ZtwqroVgswc78UeHIhct2xkhEiffyjRoFVoK";
        params.domain = "zoom.us";
        params.enableLog = true;

        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {

            }

            @Override
            public void onZoomAuthIdentityExpired() {

            }
        };
        sdk.initialize(context, listener, params);
    }

    private void joinMeeting(Context context, String meetNum, String meetPw, String meetName){
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions joinMeetingOptions = new JoinMeetingOptions();
        JoinMeetingParams joinMeetingParams = new JoinMeetingParams();
        joinMeetingParams.displayName = meetName;
        joinMeetingParams.meetingNo = meetNum;
        joinMeetingParams.password = meetPw;
        meetingService.joinMeetingWithParams(context, joinMeetingParams, joinMeetingOptions);
    }
    @Override
    public void joinGroup() {
        //static for testing. feel free to change
        String meetingPw = "abc";
        String meetingName = "UserName 1";
        String meetingNum = "2814859285";

        if(meetingNum.trim().length() >0 && meetingPw.trim().length() > 0 && meetingName.trim().length() > 0){
            joinMeeting(MeetingCreatorActivity.this, meetingNum, meetingPw, meetingName);
        }else{
            Toast.makeText(MeetingCreatorActivity.this, "Invalid Input", Toast.LENGTH_SHORT ).show();
        }
    }

    private void login(String username, String password) {
        //check if user is signed in
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {

            // 2. After request is executed, listen for the authentication result prior to starting a meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener);
        }
    }

    private void startMeeting(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);
        }
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void createMeeting() {
        //static for testing. feel free to change
        login("jchunksy@yahoo.com", "Jchunks1");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void selectDate() {
        Calendar selectedDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                meetingYear = year;
                meetingMonth = month;
                meetingDay = dayOfMonth;
                selectedDate.clear();
                selectedDate.set(year, month, dayOfMonth);
                ((TextView) findViewById(R.id.meetingDateTextView))
                        .setText(DateFormat.getDateInstance(DateFormat.FULL).format(selectedDate.getTime()));
            }
        });
        Calendar now = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        now.add(Calendar.WEEK_OF_YEAR, 2);
        datePickerDialog.getDatePicker().setMaxDate(now.getTimeInMillis());
        datePickerDialog.getDatePicker().updateDate(meetingYear, meetingMonth, meetingDay);

        datePickerDialog.show();
    }

    public void selectStartTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (endTimeHour != null && endTimeMinute != null) {
                    if ((hourOfDay * 60 + minute) > (endTimeHour * 60 + endTimeMinute)) {
                        String hour = endTimeHour > 12 ? String.valueOf(endTimeHour - 12) : String.valueOf(endTimeHour);
                        String minutes = (endTimeMinute < 10)? endTimeMinute + "0" : String.valueOf(endTimeMinute);
                        String AMPM = (endTimeHour < 12) ? "AM" : "PM";
                        Toast.makeText(MeetingCreatorActivity.this,
                                "Meeting cannot start after " + hour + ": " + minutes + " " + AMPM, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                int displayHour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                String displayMinute = (minute < 10) ? (minute) + "0" : String.valueOf(minute);
                String AMPM = hourOfDay < 12 ? "AM" : "PM";
                String displayTime = (displayHour + ": " + displayMinute + " " + AMPM);
                startTimeHour = hourOfDay;
                startTimeMinute = minute;
                ((TextView) findViewById(R.id.meetingStartTimeTextView))
                        .setText(displayTime);
                if (endTimeHour == null && endTimeMinute == null) {
                    endTimeHour = startTimeHour + 1;
                    endTimeMinute = startTimeMinute;

                    displayHour = (endTimeHour < 12) ? endTimeHour : endTimeHour - 12;
                    AMPM = (endTimeHour < 12) ? "AM" : "PM";
                    displayTime = displayHour + ": " + displayMinute + " " + AMPM;
                    ((TextView) findViewById(R.id.meetingEndTimeTextView))
                            .setText(displayTime);
                }
            }
        }, startTimeHour != null ? startTimeHour : 12, startTimeMinute != null ? startTimeMinute : 0, false);
        timePickerDialog.show();
    }

    public void selectEndTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (startTimeHour != null && startTimeMinute != null) {
                    if ((hourOfDay * 60 + minute) < (startTimeHour * 60 + startTimeMinute)) {
                        String hour = startTimeHour > 12 ? String.valueOf(startTimeHour - 12) : String.valueOf(startTimeHour);
                        String minutes = (startTimeMinute < 10)? startTimeMinute + "0" : String.valueOf(startTimeMinute);
                        String AMPM = (startTimeHour < 12) ? "AM" : "PM";
                        Toast.makeText(MeetingCreatorActivity.this,
                                "Meeting cannot end before " + hour + ": " + minutes + " " + AMPM, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                int displayHour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
                String displayMinute = (minute < 10) ? (minute) + "0" : String.valueOf(minute);
                String AMPM = hourOfDay < 12 ? "AM" : "PM";
                String displayTime = (displayHour + ": " + displayMinute + " " + AMPM);
                endTimeHour = hourOfDay;
                endTimeMinute = minute;
                ((TextView) findViewById(R.id.meetingEndTimeTextView))
                        .setText(displayTime);
                if (startTimeHour == null && endTimeMinute == null) {
                    startTimeHour = endTimeHour - 1 == 0 ? 24 : endTimeHour - 1;
                    startTimeMinute = endTimeMinute;

                    displayHour = (startTimeHour < 12) ? startTimeHour : startTimeHour - 12;
                    AMPM = (startTimeHour < 12) ? "AM" : "PM";
                    displayTime = displayHour + ": " + minute + " " + AMPM;
                    ((TextView) findViewById(R.id.meetingStartTimeTextView))
                            .setText(displayTime);
                }
            }
        }, endTimeHour != null ? endTimeHour : 12, endTimeMinute != null ? endTimeMinute : 0, false);
        timePickerDialog.show();
    }

}