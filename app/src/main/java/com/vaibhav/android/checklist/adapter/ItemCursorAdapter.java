package com.vaibhav.android.checklist.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.vaibhav.android.checklist.R;
import com.vaibhav.android.checklist.data.ChecklistContract.ItemEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemCursorAdapter extends CursorAdapter {


    public ItemCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    public static class ItemViewHolder {
        @BindView(R.id.tv_item)
        TextView mItem;

        @BindView(R.id.checkbox)
        CheckBox mCheckbox;

        public ItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checklist, parent, false);

        ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);

        itemView.setTag(itemViewHolder);

        return itemView;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ItemViewHolder itemViewHolder = null;

        if (view != null) {
            itemViewHolder = (ItemViewHolder) view.getTag();
        }

        int columnIndexItemID = cursor.getColumnIndex(ItemEntry._ID);
        int columnIndexItem = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM);
        int columnIndexStatus = cursor.getColumnIndex(ItemEntry.COLUMN_STATUS);

        final int itemID = cursor.getInt(columnIndexItemID);
        String title = cursor.getString(columnIndexItem);
        final int status = cursor.getInt(columnIndexStatus);

        if (itemViewHolder != null) {
            itemViewHolder.mItem.setText(title);

            switch (status) {
                case ItemEntry.STATUS_CHECKED: {
                    itemViewHolder.mCheckbox.setChecked(true);
                    break;
                }
                case ItemEntry.STATUS_UNCHECKED: {
                    itemViewHolder.mCheckbox.setChecked(false);
                    break;
                }
            }

            itemViewHolder.mCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    boolean isChecked = checkBox.isChecked();
                    if (isChecked) {
                        updateStatus(itemID, ItemEntry.STATUS_CHECKED, context);
                    } else {
                        updateStatus(itemID, ItemEntry.STATUS_UNCHECKED, context);
                    }
                }
            });
        }
    }

    private void updateStatus(int itemID, int status, Context context) {
        Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, itemID);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemEntry.COLUMN_STATUS, status);

        context.getContentResolver().update(
                currentItemUri,
                contentValues,
                null,
                null
        );
    }
}
