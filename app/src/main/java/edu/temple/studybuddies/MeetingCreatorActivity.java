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
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MeetingCreatorActivity extends AppCompatActivity implements MeetingDetailsFragment.MeetingDetailsFragmentInterface{

    FragmentManager fragmentManager;

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

    @Override
    public void back() {

    }
}