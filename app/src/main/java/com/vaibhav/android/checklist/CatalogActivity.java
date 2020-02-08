package com.vaibhav.android.checklist;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
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

import com.vaibhav.android.checklist.adapter.ChecklistAdapter;
import com.vaibhav.android.checklist.adapter.ChecklistCursorAdapter;
import com.vaibhav.android.checklist.data.ChecklistContract.ChecklistEntry;
import com.vaibhav.android.checklist.model.Checklist;

import java.util.ArrayList;

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>/*, ChecklistAdapter.OnChecklistClickListener*/ {

    private static final int CHECKLISTS_LOADER_ID = 1;

    /*@BindView(R.id.list_checklists)
    RecyclerView mChecklistRecyclerView;*/

    @BindView(R.id.list_checklists)
    ListView mListChecklists;

    @BindView(R.id.empty_checklist)
    LinearLayout mEmptyCatalog;

    private ChecklistCursorAdapter mChecklistCursorAdapter;

    /*private ChecklistAdapter mChecklistAdapter;

    private ArrayList<Checklist> mChecklists = new ArrayList<>();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        ButterKnife.bind(this);

//        setupChecklist();

        mChecklistCursorAdapter = new ChecklistCursorAdapter(this, null);
        mListChecklists.setAdapter(mChecklistCursorAdapter);
        mListChecklists.setEmptyView(mEmptyCatalog);

        mListChecklists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowID) {
//                String checklist =  view.findViewById(R.id.tv_title).toString();


                Intent checklistActivityIntent = new Intent(
                        CatalogActivity.this, ChecklistActivity.class);

                Uri currentChecklistUri = ContentUris
                        .withAppendedId(ChecklistEntry.CONTENT_URI, rowID);
                checklistActivityIntent.setData(currentChecklistUri);

                startActivity(checklistActivityIntent);
            }
        });

        getLoaderManager().initLoader(CHECKLISTS_LOADER_ID, null, this);

        /*Loader checklistLoader = getLoaderManager().getLoader(CHECKLISTS_LOADER_ID);
        if (checklistLoader == null) {
            getLoaderManager().initLoader(CHECKLISTS_LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(CHECKLISTS_LOADER_ID, null, this);
        }*/
    }

    /*private void showList() {
        mChecklistRecyclerView.setVisibility(View.VISIBLE);
        mEmptyCatalog.setVisibility(View.GONE);
    }

    private void hideList() {
        mEmptyCatalog.setVisibility(View.VISIBLE);
        mChecklistRecyclerView.setVisibility(View.GONE);
    }*/

    /*private void setupChecklist() {
        mChecklistRecyclerView.setHasFixedSize(true);

        LayoutManager layoutManager = new LinearLayoutManager(this);
        mChecklistRecyclerView.setLayoutManager(layoutManager);

        mChecklistRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mChecklistRecyclerView.addItemDecoration(new DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL)
        );

        mChecklistAdapter = new ChecklistAdapter(this);
        mChecklistRecyclerView.setAdapter(mChecklistAdapter);
    }*/

    /*@Override
    public void OnChecklistClick(int checklistID) {
        Intent checklistActivityIntent = new Intent(
                CatalogActivity.this, ChecklistActivity.class);

        Uri currentChecklistUri = ContentUris
                .withAppendedId(ChecklistEntry.CONTENT_URI, checklistID);
        checklistActivityIntent.setData(currentChecklistUri);

        startActivity(checklistActivityIntent);
    }*/

    @OnClick(R.id.fab_add_new_checklist)
    protected void addNewChecklist() {
        startActivity(new Intent(CatalogActivity.this, ChecklistEditorActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_delete_all_checklists: {
                confirmDeleteAllChecklists();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCursor.getCount() == 0) {
            MenuItem deleteAll = menu.findItem(R.id.action_delete_all_checklists);
            deleteAll.setVisible(false);
        }

        return true;
    }

    private void confirmDeleteAllChecklists() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title_conf_del);
        dialog.setMessage(R.string.dialog_msg_delete_all_checklists);
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
                deleteAllChecklists();
            }
        });
        dialog.show();
    }

    private void deleteAllChecklists() {
        int numberOfChecklistsDeleted = getContentResolver().delete(
                ChecklistEntry.CONTENT_URI, null, null);

        if (numberOfChecklistsDeleted == 0) {
            Toast.makeText(this, R.string.msg_error_delete_all_checklists, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.msg_all_checklists_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        switch (loaderID) {
            case CHECKLISTS_LOADER_ID: {
                String[] projection = {
                        ChecklistEntry._ID,
                        ChecklistEntry.COLUMN_TITLE
                };

                return new CursorLoader(
                        this,
                        ChecklistEntry.CONTENT_URI,
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

    private Cursor mCursor;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        extractChecklists(cursor);

        mChecklistCursorAdapter.swapCursor(cursor);

        mCursor = cursor;
        invalidateOptionsMenu();
    }

    /*private void extractChecklists(Cursor cursor) {
        if (cursor.getCount() == 0) {
            while (cursor.moveToNext()) {
                int columnIndexID = cursor.getColumnIndex(ChecklistEntry._ID);
                int columnIndexTitle = cursor.getColumnIndex(ChecklistEntry.COLUMN_TITLE);

                int ID = cursor.getInt(columnIndexID);
                String title = cursor.getString(columnIndexTitle);

                mChecklists.add(new Checklist(ID, title));
            }

            mChecklistAdapter.setChecklists(mChecklists);
            showList();
        } else {
            hideList();
        }

        invalidateOptionsMenu();
    }*/

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mChecklistCursorAdapter.swapCursor(null);
        /*mChecklists.clear();
        mChecklistAdapter.setChecklists(mChecklists);*/
    }
}