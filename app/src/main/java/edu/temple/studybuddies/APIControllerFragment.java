package edu.temple.studybuddies;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link APIControllerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class APIControllerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public APIControllerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment APIControllerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static APIControllerFragment newInstance(String param1, String param2) {
        APIControllerFragment fragment = new APIControllerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_a_p_i_controller, container, false);
        ControllerInterface parentActivity = (ControllerInterface) getActivity();
        Button joinGroupButton = view.findViewById(R.id.btnJoinGroup);
        joinGroupButton.setOnClickListener(v -> {
            parentActivity.launchJoinGroupDialogue();
        });
        Button viewGroupsButton = view.findViewById(R.id.btnViewGroup);
        viewGroupsButton.setOnClickListener(v -> {
            parentActivity.viewGroups();
        });
        Button homeButton = view.findViewById(R.id.btnHome);
        homeButton.setOnClickListener(v -> {
            parentActivity.home();
        });

        return view;
    }

    public interface ControllerInterface {
        void launchJoinGroupDialogue();
        void viewGroups();
        void home();
    }
}