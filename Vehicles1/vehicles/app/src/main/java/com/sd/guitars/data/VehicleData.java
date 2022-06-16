package com.sd.guitars.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class VehicleData {

    private static final String TAG = VehicleData.class.getSimpleName();
    private final DBHelper dbHelper;

    public VehicleData(Context context) {
        dbHelper = new DBHelper(context);
        Log.i(TAG, "Initialised vehicle data");
    }

    public void addVehicle(String make, String model, String noOfSeats, String ModelNo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(VehicleDataStruct.VehicleDataTable.COL_MAKE, make);
        values.put(VehicleDataStruct.VehicleDataTable.COL_MODEL, model);
        values.put(VehicleDataStruct.VehicleDataTable.COL_NUMBER_OF_SEATS, noOfSeats);
        values.put(VehicleDataStruct.VehicleDataTable.COL_MODEL_NUMBER, ModelNo);

        /* --- insert to db --- */
        db.insert(VehicleDataStruct.VehicleDataTable.TABLE_NAME, null, values);
        db.close();
    }


    public int deleteVehicle(Long vehicleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = VehicleDataStruct.VehicleDataTable._ID + " = ?";
        // Specify arguments in placeholder order.
        // convert integer to string
        String vid = vehicleId.toString();
        String[] selectionArgs = {vid};
        // Issue SQL statement.
        int deletedRows = db.delete(VehicleDataStruct.VehicleDataTable.TABLE_NAME, selection, selectionArgs);
        db.close();
        return deletedRows;
    }

    public int updateVehicle(int vehicleId, String make, String model, String ModelNo, String noOfseats) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New values for one column
        ContentValues values = new ContentValues();
        values.put(VehicleDataStruct.VehicleDataTable.COL_MAKE, make);
        values.put(VehicleDataStruct.VehicleDataTable.COL_MODEL, model);
        values.put(VehicleDataStruct.VehicleDataTable.COL_NUMBER_OF_SEATS, noOfseats);
        values.put(VehicleDataStruct.VehicleDataTable.COL_MODEL_NUMBER, ModelNo);

        // Which row to update, based on the title
        String selection = VehicleDataStruct.VehicleDataTable._ID + " = ?";
        String vid = new Integer(vehicleId).toString();
        String[] selectionArgs = {vid};

        int count = db.update(
                VehicleDataStruct.VehicleDataTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();
        return count;
    }

    public List getVehicleIDs() {
        List itemIds = new ArrayList<Long>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query. We only want the IDs, in this case
        String[] projection = {
                VehicleDataStruct.VehicleDataTable._ID
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                VehicleDataStruct.VehicleDataTable.COL_MAKE + ", " + VehicleDataStruct.VehicleDataTable.COL_MODEL + " asc";

        Cursor cursor = db.query(
                VehicleDataStruct.VehicleDataTable.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while (cursor.moveToNext()) {
            Long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(VehicleDataStruct.VehicleDataTable._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        db.close();

        return itemIds;
    }

    public Vehicle getVehicle(Long VehicleID) {
        Vehicle result = null; // set it to no guitar

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                VehicleDataStruct.VehicleDataTable.COL_MAKE,
                VehicleDataStruct.VehicleDataTable.COL_MODEL,
                VehicleDataStruct.VehicleDataTable.COL_NUMBER_OF_SEATS,
                VehicleDataStruct.VehicleDataTable.COL_MODEL_NUMBER
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = VehicleDataStruct.VehicleDataTable._ID + " = ?";
        String[] selectionArgs = {VehicleID.toString()};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                VehicleDataStruct.VehicleDataTable.COL_MAKE + ", " + VehicleDataStruct.VehicleDataTable.COL_MODEL + " DESC";

        Cursor cursor = db.query(
                VehicleDataStruct.VehicleDataTable.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );

        if (cursor.moveToFirst()) {
            Vehicle v = new Vehicle();
            v.make = cursor.getString(cursor.getColumnIndexOrThrow(VehicleDataStruct.VehicleDataTable.COL_MAKE));
            v.model = cursor.getString(cursor.getColumnIndexOrThrow(VehicleDataStruct.VehicleDataTable.COL_MODEL));
            v.noOfSeats = cursor.getString(cursor.getColumnIndexOrThrow(VehicleDataStruct.VehicleDataTable.COL_NUMBER_OF_SEATS));
            v.serialNo = cursor.getString(cursor.getColumnIndexOrThrow(VehicleDataStruct.VehicleDataTable.COL_MODEL_NUMBER));
            cursor.close();
            db.close();
            return v;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    private void clearAllVehicleData() {
        // empty table
        String sqlDel = "delete from " + VehicleDataStruct.VehicleDataTable.TABLE_NAME;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sqlDel);
        db.close();
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    // DATABASE_NAME RELATED DEFINITIONS
    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public final class VehicleDataStruct {
        private VehicleDataStruct() {
        }

        // General DB related information
        static final int DB_VERSION = 1;
        static final String DATABASE_NAME = "VehicleData.db";

        /* Inner class that defines the table contents */
        public abstract class VehicleDataTable implements BaseColumns {
            public static final String TABLE_NAME = "GuitarData";
            public static final String _ID = "_id";
            public static final String COL_MAKE = "make";
            public static final String COL_MODEL = "model";
            public static final String COL_NUMBER_OF_SEATS = "noOfSeats";
            public static final String COL_MODEL_NUMBER = "ModelNo";
        }
    }

    /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    public final class DBHelper extends SQLiteOpenHelper {
        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

        public DBHelper(Context context) {
            super(context, VehicleDataStruct.DATABASE_NAME, null, VehicleDataStruct.DB_VERSION);
        }

        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        @Override
        public void onCreate(SQLiteDatabase db) {
            // generate the create table SQL statement
            String sqlCreate = "create table " + VehicleDataStruct.VehicleDataTable.TABLE_NAME + " ("
                    + VehicleDataStruct.VehicleDataTable._ID + " INTEGER primary key, "
                    + VehicleDataStruct.VehicleDataTable.COL_MAKE + " VARCHAR(255), "
                    + VehicleDataStruct.VehicleDataTable.COL_MODEL + " VARCHAR(255), "
                    + VehicleDataStruct.VehicleDataTable.COL_NUMBER_OF_SEATS + " VARCHAR(255), "
                    + VehicleDataStruct.VehicleDataTable.COL_MODEL_NUMBER + " VARCHAR(255)) ";

            // execute the create table statement
            db.execSQL(sqlCreate);
        }

        /*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table " + VehicleDataStruct.VehicleDataTable.TABLE_NAME);
            this.onCreate(db);
        }
    }
}
