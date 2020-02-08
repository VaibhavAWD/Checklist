package com.vaibhav.android.checklist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ChecklistContract {
    public static final String CONTENT_AUTHORITY = "com.vaibhav.android.checklist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CHECKLISTS = "checklists";

    public static final String PATH_ITEMS = "items";

    public static class ChecklistEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CHECKLISTS);

        public static final String CONTENT_TYPE_LIST = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_CHECKLISTS;

        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_CHECKLISTS;

        public static final String TABLE_NAME = "checklists";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_TITLE = "title";
    }

    public static class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final String CONTENT_TYPE_LIST = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_CHECKLIST_ID = "checklist_id";

        public static final String COLUMN_ITEM = "item";

        public static final String COLUMN_STATUS = "status";

        public static final int STATUS_UNCHECKED = 0;
        public static final int STATUS_CHECKED = 1;

        public static boolean isNotValidStatus(int status) {
            return status < STATUS_UNCHECKED || status > STATUS_CHECKED;
        }
    }
}
