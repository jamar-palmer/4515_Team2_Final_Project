package edu.temple.studybuddies;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private ArrayList<Group> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private String id;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.row_text);
            view.setOnClickListener(v -> {
                if (id != null) {
                    Log.d("ADAPTER", "ID should not be null: " + id);
                    Bundle bundle = new Bundle();
                    bundle.putString(GroupDetailsFragment.GROUP_ID, id);
                    GroupDetailsFragment groupDetailsFragment = GroupDetailsFragment.newInstance();
                    groupDetailsFragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.mainContainer, groupDetailsFragment, "currentGroupFragment")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public GroupAdapter(ArrayList<Group> dataSet) {
        localDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(localDataSet.get(position).name);
        holder.id = localDataSet.get(position).id;
        Log.d("ADAPTER", "GroupId in Group Adapter: " + holder.id);
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
