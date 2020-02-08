package com.vaibhav.android.checklist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.vaibhav.android.checklist.data.ChecklistContract.ChecklistEntry;
import com.vaibhav.android.checklist.data.ChecklistContract.ItemEntry;


public class ChecklistProvider extends ContentProvider {
    private static final String LOG_TAG = "PROVIDER";

    private ChecklistDbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int CHECKLISTS = 101;
    private static final int CHECKLIST = 102;

    private static final int ITEMS = 201;
    private static final int ITEM = 202;

    static {
        sUriMatcher.addURI(
                ChecklistContract.CONTENT_AUTHORITY,
                ChecklistContract.PATH_CHECKLISTS,
                CHECKLISTS);
        sUriMatcher.addURI(
                ChecklistContract.CONTENT_AUTHORITY,
                ChecklistContract.PATH_CHECKLISTS + "/#",
                CHECKLIST);
        sUriMatcher.addURI(
                ChecklistContract.CONTENT_AUTHORITY,
                ChecklistContract.PATH_ITEMS,
                ITEMS);
        sUriMatcher.addURI(
                ChecklistContract.CONTENT_AUTHORITY,
                ChecklistContract.PATH_ITEMS + "/#",
                ITEM);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ChecklistDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHECKLISTS: {
                cursor = getData(
                        ChecklistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                break;
            }
            case CHECKLIST: {
                selection = ChecklistEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = getData(
                        ChecklistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                break;
            }
            case ITEMS: {
                cursor = getData(
                        ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                break;
            }
            case ITEM: {
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = getData(
                        ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
                break;
            }
            default:
                throw new IllegalArgumentException("Cannot query data with " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    private Cursor getData(String tableName, String[] projection, String selection,
                           String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        return database.query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHECKLISTS:
            case ITEMS:
                return insertData(uri, contentValues, match);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertData(Uri uri, ContentValues contentValues, int match) {
        if (contentValues.size() == 0) {
            return null;
        } else {
            String tableName = null;

            switch (match) {
                case CHECKLISTS: {
                    tableName = ChecklistEntry.TABLE_NAME;

                    String title = contentValues.getAsString(ChecklistEntry.COLUMN_TITLE);
                    if (title == null) {
                        throw new IllegalArgumentException("Title cannot be empty");
                    }
                    break;
                }
                case ITEMS: {
                    tableName = ItemEntry.TABLE_NAME;

                    String item = contentValues.getAsString(ItemEntry.COLUMN_ITEM);
                    if (item == null) {
                        throw new IllegalArgumentException("Item cannot be empty");
                    }

                    Integer status = contentValues.getAsInteger(ItemEntry.COLUMN_STATUS);
                    if (status != null && ItemEntry.isNotValidStatus(status)) {
                        throw new IllegalArgumentException("Invalid status");
                    }

                    break;
                }
            }

            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            long newRowID = database.insert(tableName, null, contentValues);

            if (newRowID == -1) {
                Log.d(LOG_TAG, "Unable to insert data with " + uri);
                return null;
            }

            getContext().getContentResolver().notifyChange(uri, null);

            return ContentUris.withAppendedId(uri, newRowID);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHECKLIST: {
                selection = ChecklistEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateData(uri, contentValues, selection, selectionArgs, match);
            }
            case ITEMS: {
                return updateData(uri, contentValues, selection, selectionArgs, match);
            }
            case ITEM: {
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                return updateData(uri, contentValues, selection, selectionArgs, match);
            }
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateData(Uri uri, ContentValues contentValues, String selection,
                           String[] selectionArgs, int match) {
        if (contentValues.size() == 0) {
            return 0;
        } else {
            String tableName = null;

            switch (match) {
                case CHECKLIST: {
                    tableName = ChecklistEntry.TABLE_NAME;

                    String title = contentValues.getAsString(ChecklistEntry.COLUMN_TITLE);
                    if (title == null) {
                        throw new IllegalArgumentException("Title cannot be empty");
                    }
                    break;
                }
                case ITEMS: {
                    tableName = ItemEntry.TABLE_NAME;

                    Integer status = contentValues.getAsInteger(ItemEntry.COLUMN_STATUS);
                    if (ItemEntry.isNotValidStatus(status)) {
                        throw new IllegalArgumentException("Invalid status");
                    }
                    break;
                }
                case ITEM: {
                    tableName = ItemEntry.TABLE_NAME;

                    if (contentValues.containsKey(ItemEntry.COLUMN_ITEM)) {
                        String item = contentValues.getAsString(ItemEntry.COLUMN_ITEM);
                        if (item == null) {
                            throw new IllegalArgumentException("Item cannot be empty");
                        }
                    }

                    if (contentValues.containsKey(ItemEntry.COLUMN_STATUS)) {
                        Integer status = contentValues.getAsInteger(ItemEntry.COLUMN_STATUS);
                        if (status != null && ItemEntry.isNotValidStatus(status)) {
                            throw new IllegalArgumentException("Invalid status");
                        }
                    }

                    break;
                }
            }

            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            int numberOfRowsUpdated =
                    database.update(tableName, contentValues, selection, selectionArgs);

            if (numberOfRowsUpdated == 0) {
                Log.d(LOG_TAG, "Unable to update data with " + uri);
                return 0;
            }

            getContext().getContentResolver().notifyChange(uri, null);

            return numberOfRowsUpdated;
        }
    }

    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHECKLISTS: {
                numberOfRowsDeleted =
                        database.delete(ChecklistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CHECKLIST: {
                selection = ChecklistEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                numberOfRowsDeleted =
                        database.delete(ChecklistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ITEMS: {
                numberOfRowsDeleted =
                        database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ITEM: {
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                numberOfRowsDeleted =
                        database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (numberOfRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CHECKLISTS:
                return ChecklistEntry.CONTENT_TYPE_LIST;
            case CHECKLIST:
                return ChecklistEntry.CONTENT_TYPE_ITEM;
            case ITEMS:
                return ItemEntry.CONTENT_TYPE_LIST;
            case ITEM:
                return ItemEntry.CONTENT_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}
