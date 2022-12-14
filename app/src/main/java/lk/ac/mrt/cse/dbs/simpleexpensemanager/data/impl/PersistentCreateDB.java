package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class PersistentCreateDB extends SQLiteOpenHelper {
    //References https://developer.android.com/training/data-storage/sqlite

    public PersistentCreateDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    public static PersistentCreateDB instance;



    private static final String DATABASE_NAME = "200600T";
    private static final int DATABASE_VERSION = 1;

    //   tables of the database
    public static final String TABLE_ACCOUNTS = "account";
    public static final String TABLE_TRANSACTIONS = "transactions";


    //    creating keys
    public static final String KEY_ACCOUNT_NO = "accountNo";
    public static final String KEY_BANK_NAME = "bankName";
    public static final String KEY_ACCOUNT_HOLDER_NAME = "accountHolderName";
    public static final String KEY_BALANCE = "balance";
    private static final String KEY_TRANSACTION_ID = "id";
    public static final String KEY_EXPENSE_TYPE = "expenseType";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_DATE = "date";




    //getting datbase instance
    public static PersistentCreateDB getInstance(Context context) {
        if (instance == null) {
            instance = new PersistentCreateDB(context);
        }
        return instance;
    }

    ;
    //creating account table
    private static final String CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS + "("
            + KEY_ACCOUNT_NO + " TEXT PRIMARY KEY," + KEY_BANK_NAME + " TEXT,"
            + KEY_ACCOUNT_HOLDER_NAME + " TEXT," + KEY_BALANCE + " REAL" + ")";

    //creating transaction table
    private static final String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + "("
            + KEY_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_DATE + " TEXT," + KEY_ACCOUNT_NO + " TEXT,"
            + KEY_EXPENSE_TYPE + " TEXT," + KEY_AMOUNT + " REAL," + "FOREIGN KEY(" + KEY_ACCOUNT_NO +
            ") REFERENCES " + TABLE_ACCOUNTS + "(" + KEY_ACCOUNT_NO + ") )";


    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNTS_TABLE);
        db.execSQL(CREATE_TRANSACTIONS_TABLE);
    }

    // Droping older table if existed
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_ACCOUNTS + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_TRANSACTIONS + "'");

        // Creating tables again
        onCreate(db);
    }
}
