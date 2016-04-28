package ruebenburrowsdavies.info.primefittracking;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDBHelper extends SQLiteOpenHelper {

    public TaskDBHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {

        sqlDB.execSQL("create table tasks ("
                + "id integer primary key autoincrement,"
                + "task,"
                + "notification,"
                + "_id," // added a ','
                + "date" + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {

        if (i2>i){
            sqlDB.execSQL("ALTER TABLE " + TaskContract.TABLE + " ADD COLUMN " + TaskContract.Columns.NOTIFICATION);
        }

        sqlDB.execSQL("DROP TABLE IF EXISTS "+TaskContract.TABLE);


        onCreate(sqlDB);
    }
}