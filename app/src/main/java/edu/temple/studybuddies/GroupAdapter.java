package edu.temple.studybuddies;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private ArrayList<Group> localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // TODO: Define an on click listener for ViewHolder's View

            textView = view.findViewById(R.id.row_text);
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
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
