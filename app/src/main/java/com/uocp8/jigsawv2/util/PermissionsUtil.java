package com.uocp8.jigsawv2.util;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uocp8.jigsawv2.model.MyCalendar;

public final class PermissionsUtil{
    public static void checkPermission(int callbackId, Context context, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(context, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions((Activity) context, permissionsId, callbackId);
    }

    public static MyCalendar[] getCalendar(Context c) {

        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = c.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);
        MyCalendar[] m_calendars = new MyCalendar[managedCursor.getCount()];
        if (managedCursor.moveToFirst()){

            String calName;
            String calID;
            int cont= 0;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                calID = managedCursor.getString(idCol);
                m_calendars[cont] = new MyCalendar(calName, calID);
                cont++;
            } while(managedCursor.moveToNext());
            managedCursor.close();
        }
        return m_calendars;

    }
}
