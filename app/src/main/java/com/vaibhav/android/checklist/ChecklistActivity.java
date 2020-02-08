package com.vaibhav.android.checklist;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.vaibhav.android.checklist.adapter.ItemCursorAdapter;
import com.vaibhav.android.checklist.data.ChecklistContract.ItemEntry;
import com.vaibhav.android.checklist.data.ChecklistContract.ChecklistEntry;

public class ChecklistActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEMS_LOADER_ID = 1;

    public static final String EXTRA_CHECKLIST_ID = "checklist_id";

    @BindView(R.id.list_items)
    ListView mListItems;

    @BindView(R.id.empty_checklist)
    LinearLayout mEmptyChecklist;

    private Uri mCurrentChecklistUri = null;

    private int mChecklistID;

    private ItemCursorAdapter mItemCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentChecklistUri = intent.getData();

        mChecklistID = (int) ContentUris.parseId(mCurrentChecklistUri);

        getLoaderManager().initLoader(ITEMS_LOADER_ID, null, this);

        //noinspection ConstantConditions
        mItemCursorAdapter = new ItemCursorAdapter(this, null);
        mListItems.setAdapter(mItemCursorAdapter);
        mListItems.setEmptyView(mEmptyChecklist);

        mListItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowID) {
                Intent itemEditorActivityIntent = new Intent(
                        ChecklistActivity.this, ItemEditorActivity.class);

                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, rowID);
                itemEditorActivityIntent.setData(currentItemUri);

                startActivity(itemEditorActivityIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setChecklistTitle();
    }

    private void setChecklistTitle() {
        String[] projection = {
                ChecklistEntry._ID,
                ChecklistEntry.COLUMN_TITLE
        };

        Cursor cursor = getContentResolver().query(
                mCurrentChecklistUri,
                projection,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexTitle = cursor.getColumnIndex(ChecklistEntry.COLUMN_TITLE);
            String title = cursor.getString(columnIndexTitle);
            setTitle(title);
            cursor.close();
        }
    }

    @OnClick(R.id.fab_add_new_item)
    protected void addNewItem() {
        Intent itemEditorActivityIntent = new Intent(
                ChecklistActivity.this,
                ItemEditorActivity.class
        );
        itemEditorActivityIntent.putExtra(EXTRA_CHECKLIST_ID, mChecklistID);
        startActivity(itemEditorActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checklist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_mark_all_checked: {
                markAll(ItemEntry.STATUS_CHECKED);
                return true;
            }
            case R.id.action_mark_all_unchecked: {
                markAll(ItemEntry.STATUS_UNCHECKED);
                return true;
            }
            case R.id.action_delete_items: {
                confirmDeleteAllItems();
                return true;
            }
            case R.id.action_rename_checklist: {
                Intent checklistEditorActivityIntent = new Intent(
                        ChecklistActivity.this, ChecklistEditorActivity.class);
                checklistEditorActivityIntent.setData(mCurrentChecklistUri);
                startActivity(checklistEditorActivityIntent);
                return true;
            }
            case R.id.action_delete_checklist: {
                confirmDeleteChecklist();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void markAll(int status) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemEntry.COLUMN_STATUS, status);

        String selection = ItemEntry.COLUMN_CHECKLIST_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mChecklistID)};

        int numberOfItemsUpdated = getContentResolver().update(
                ItemEntry.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs
        );

        if (numberOfItemsUpdated != 0) {
            if (status == ItemEntry.STATUS_CHECKED) {
                Toast.makeText(this, R.string.msg_all_items_checked, Toast.LENGTH_SHORT).show();
            } else if (status == ItemEntry.STATUS_UNCHECKED) {
                Toast.makeText(this, R.string.msg_all_items_unchecked, Toast.LENGTH_SHORT).show();
            }
        } else {
            if (status == ItemEntry.STATUS_CHECKED) {
                Toast.makeText(this, R.string.msg_all_items_not_checked, Toast.LENGTH_SHORT).show();
            } else if (status == ItemEntry.STATUS_UNCHECKED) {
                Toast.makeText(this, R.string.msg_all_items_not_unchecked, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmDeleteAllItems() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title_conf_del);
        dialog.setMessage(R.string.dialog_msg_delete_items);
        dialog.setNegativeButton(R.string.option_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setPositiveButton(R.string.option_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllItems();
            }
        });
        dialog.show();
    }

    private void deleteAllItems() {
        String selection = ItemEntry.COLUMN_CHECKLIST_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mChecklistID)};

        int numberOfItemsDeleted = getContentResolver().delete(
                ItemEntry.CONTENT_URI,
                selection,
                selectionArgs
        );

        if (numberOfItemsDeleted != 0) {
            Toast.makeText(this, R.string.msg_all_items_deleted, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.msg_items_not_deleted, Toast.LENGTH_SHORT).show();
        }

    }

    private void confirmDeleteChecklist() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title_conf_del);
        dialog.setMessage(R.string.dialog_msg_delete_checklist);
        dialog.setNegativeButton(R.string.option_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setPositiveButton(R.string.option_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteChecklist();
            }
        });
        dialog.show();
    }

    private void deleteChecklist() {
        if (mCursor.getCount() == 0) {
            deleteAllItems();
        }

        int numberOfChecklistsDeleted = getContentResolver().delete(
                mCurrentChecklistUri,
                null,
                null
        );

        if (numberOfChecklistsDeleted == 0) {
            Toast.makeText(this, R.string.msg_checklist_not_deleted, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.msg_checklist_deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case ITEMS_LOADER_ID: {
                String[] projection = {
                        ItemEntry._ID,
                        ItemEntry.COLUMN_ITEM,
                        ItemEntry.COLUMN_STATUS
                };
                String selection = ItemEntry.COLUMN_CHECKLIST_ID + "=?";
                String[] selectionArgs = new String[]{String.valueOf(mChecklistID)};

                return new CursorLoader(
                        this,
                        ItemEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null
                );
            }
            default:
                return null;
        }
    }

    private Cursor mCursor;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mItemCursorAdapter.swapCursor(cursor);

        mCursor = cursor;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCursor.getCount() == 0) {
            MenuItem markAllAsChecked = menu.findItem(R.id.action_mark_all_checked);
            markAllAsChecked.setVisible(false);

            MenuItem markAllAsUnchecked = menu.findItem(R.id.action_mark_all_unchecked);
            markAllAsUnchecked.setVisible(false);

            MenuItem deleteItems = menu.findItem(R.id.action_delete_items);
            deleteItems.setVisible(false);
        }
        return true;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListItems.setAdapter(null);
    }
}
