package edu.temple.studybuddies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
        login("jchunksy@yahoo.com", "Jchunks1");
    }
}