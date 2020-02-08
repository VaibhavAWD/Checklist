package com.vaibhav.android.checklist.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.vaibhav.android.checklist.R;
import com.vaibhav.android.checklist.data.ChecklistContract.ChecklistEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChecklistCursorAdapter extends CursorAdapter {


    public ChecklistCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    public static class ChecklistViewHolder {
        @BindView(R.id.tv_title)
        TextView mTitle;

        public ChecklistViewHolder(View checklistView) {
            ButterKnife.bind(this, checklistView);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View checklistView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catalog, parent, false);

        ChecklistViewHolder checklistViewHolder = new ChecklistViewHolder(checklistView);

        checklistView.setTag(checklistViewHolder);

        return checklistView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ChecklistViewHolder checklistViewHolder = null;
        if (view != null) {
            checklistViewHolder = (ChecklistViewHolder) view.getTag();
        }

        int columnIndexTitle = cursor.getColumnIndex(ChecklistEntry.COLUMN_TITLE);

        String title = cursor.getString(columnIndexTitle);

        if (checklistViewHolder != null) {
            checklistViewHolder.mTitle.setText(title);
        }
    }
}
