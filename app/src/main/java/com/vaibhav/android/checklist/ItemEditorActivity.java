package com.vaibhav.android.checklist;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vaibhav.android.checklist.data.ChecklistContract.ItemEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEM_LOADER_ID = 1;

    @BindView(R.id.label_item)
    TextInputLayout mLabelItem;

    @BindView(R.id.input_item)
    TextInputEditText mInputItem;

    @BindView(R.id.tv_status)
    TextView mStatus;

    private Uri mCurrentItemUri = null;

    private int mChecklistID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        mChecklistID = intent.getIntExtra(ChecklistActivity.EXTRA_CHECKLIST_ID, -1);

        if (mCurrentItemUri != null) {
            invalidateOptionsMenu();

            setTitle(R.string.label_edit_item);

            getLoaderManager().initLoader(ITEM_LOADER_ID, null, this);
        } else {
            setTitle(R.string.label_new_item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentItemUri == null) {
            MenuItem delete = menu.findItem(R.id.action_delete);
            delete.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save: {
                saveItem();
                return true;
            }
            case R.id.action_delete: {
                deleteItem();
                return true;
            }
            case android.R.id.home: {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteItem() {
        int numberOfItemsDeleted = getContentResolver().delete(
                mCurrentItemUri, null, null
        );

        if (numberOfItemsDeleted != 0) {
            Toast.makeText(this, R.string.msg_item_deleted, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.msg_item_not_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveItem() {
        String title = mInputItem.getEditableText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            mLabelItem.setErrorEnabled(true);
            mLabelItem.setError(getString(R.string.warn_enter_title));
            return;
        } else {
            mLabelItem.setErrorEnabled(false);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ItemEntry.COLUMN_ITEM, title);

        Uri uri = null;
        int numberOfItemsUpdated = 0;
        if (mCurrentItemUri != null) {
            numberOfItemsUpdated = getContentResolver().update(
                    mCurrentItemUri,
                    contentValues,
                    null,
                    null
            );
        } else {
            contentValues.put(ItemEntry.COLUMN_CHECKLIST_ID, mChecklistID);
            uri = getContentResolver().insert(ItemEntry.CONTENT_URI, contentValues);
        }

        if (uri != null || numberOfItemsUpdated != 0) {
            Toast.makeText(this, R.string.msg_item_saved, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, R.string.msg_item_not_saved, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case ITEM_LOADER_ID: {
                String[] projection = {
                        ItemEntry._ID,
                        ItemEntry.COLUMN_ITEM,
                        ItemEntry.COLUMN_STATUS
                };

                return new CursorLoader(
                        this,
                        mCurrentItemUri,
                        projection,
                        null,
                        null,
                        null
                );
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        loadData(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        invalidateFields();
    }

    private void loadData(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int columnIndexItem = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM);
            int columnIndexStatus = cursor.getColumnIndex(ItemEntry.COLUMN_STATUS);

            String title = cursor.getString(columnIndexItem);
            mInputItem.setText(title);

            int status = cursor.getInt(columnIndexStatus);
            String sStatus = "STATUS: ";
            if (status == ItemEntry.STATUS_CHECKED) {
                sStatus = sStatus + getString(R.string.label_checked);
            } else if (status == ItemEntry.STATUS_UNCHECKED) {
                sStatus = sStatus + getString(R.string.label_unchecked);
            }

            mStatus.setText(sStatus);
        }
    }

    private void invalidateFields() {
        mLabelItem.invalidate();
        mStatus.invalidate();
    }
}
