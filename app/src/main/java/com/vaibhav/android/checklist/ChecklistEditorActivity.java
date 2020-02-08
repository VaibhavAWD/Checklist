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
import android.widget.Toast;

import com.vaibhav.android.checklist.data.ChecklistContract.ChecklistEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChecklistEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CHECKLIST_LOADER_ID = 1;

    @BindView(R.id.label_title)
    TextInputLayout mLabelTitle;

    @BindView(R.id.input_title)
    TextInputEditText mInputTitle;

    private Uri mCurrentChecklistUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_editor);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentChecklistUri = intent.getData();

        if (mCurrentChecklistUri != null) {
            setTitle(R.string.label_edit_checklist);

            getLoaderManager().initLoader(CHECKLIST_LOADER_ID, null, this);
        } else {
            setTitle(R.string.label_new_checklist);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checklist_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save: {
                saveChecklist();
                return true;
            }
            case android.R.id.home: {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveChecklist() {
        String title = mInputTitle.getEditableText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            mLabelTitle.setErrorEnabled(true);
            mLabelTitle.setError(getString(R.string.warn_enter_title));
            return;
        } else {
            mLabelTitle.setErrorEnabled(false);
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ChecklistEntry.COLUMN_TITLE, title);

        if (mCurrentChecklistUri != null) {
            int numberOfChecklistsUpdated = getContentResolver().update(
                    mCurrentChecklistUri,
                    contentValues,
                    null,
                    null
            );

            if (numberOfChecklistsUpdated != 0) {
                Toast.makeText(this, R.string.msg_checklist_saved, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, R.string.msg_checklist_not_saved, Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri uri = getContentResolver().insert(ChecklistEntry.CONTENT_URI, contentValues);
            openNewChecklist(uri);
        }
    }

    private void openNewChecklist(Uri uri) {
        Intent checklistActivityIntent = new Intent(
                ChecklistEditorActivity.this,
                ChecklistActivity.class
        );
        checklistActivityIntent.setData(uri);
        startActivity(checklistActivityIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case CHECKLIST_LOADER_ID: {
                String[] projection = {
                        ChecklistEntry._ID,
                        ChecklistEntry.COLUMN_TITLE
                };

                return new CursorLoader(
                        this,
                        mCurrentChecklistUri,
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
            int columnIndexTitle = cursor.getColumnIndex(ChecklistEntry.COLUMN_TITLE);
            String title = cursor.getString(columnIndexTitle);
            mInputTitle.setText(title);
        }
    }

    private void invalidateFields() {
        mLabelTitle.invalidate();
    }
}
