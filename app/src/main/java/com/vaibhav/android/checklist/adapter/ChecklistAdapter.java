package com.vaibhav.android.checklist.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vaibhav.android.checklist.R;
import com.vaibhav.android.checklist.model.Checklist;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder> {

    private ArrayList<Checklist> mChecklists = new ArrayList<>();

    public void setChecklists(ArrayList<Checklist> checklists) {
        mChecklists = checklists;
        notifyDataSetChanged();
    }

    private OnChecklistClickListener mOnChecklistClickListener;

    public ChecklistAdapter(OnChecklistClickListener onChecklistClickListener) {
        mOnChecklistClickListener = onChecklistClickListener;
    }

    public interface OnChecklistClickListener {
        void OnChecklistClick(int checklistID);
    }

    public class ChecklistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_title)
        TextView mTitle;

        public ChecklistViewHolder(@NonNull View checklistView) {
            super(checklistView);
            ButterKnife.bind(this, checklistView);
            checklistView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            int ID = mChecklists.get(position).getID();
            mOnChecklistClickListener.OnChecklistClick(ID);
        }
    }

    @NonNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View checklistView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catalog, parent, false);

        return new ChecklistViewHolder(checklistView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistViewHolder checklistViewHolder, int position) {
        Checklist checklist = mChecklists.get(position);

        checklistViewHolder.mTitle.setText(checklist.getTitle());
    }

    @Override
    public int getItemCount() {
        return mChecklists.size();
    }
}
