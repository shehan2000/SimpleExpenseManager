package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private PersistentCreateDB database;
    public PersistentTransactionDAO(PersistentCreateDB database){
        this.database = database;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db=database.getWritableDatabase();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersistentCreateDB.KEY_ACCOUNT_NO,accountNo);
        contentValues.put(PersistentCreateDB.KEY_EXPENSE_TYPE,(expenseType == ExpenseType.INCOME)?0:1);
        contentValues.put(PersistentCreateDB.KEY_AMOUNT,amount);
        contentValues.put(PersistentCreateDB.KEY_DATE,simpleDateFormat.format(date));
        db.insert(PersistentCreateDB.TABLE_TRANSACTIONS,null,contentValues);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();
        //String query = "SELECT * from transaction_log";
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor c = db.query(PersistentCreateDB.TABLE_TRANSACTIONS, null, null, null, null, null, null);
        if(c.moveToFirst()){
            while (true){
                Date date = new Date(c.getLong(0));
                String accountNumber = c.getString(1);
                ExpenseType expenseType = ExpenseType.INCOME;
                if(c.getInt(2) == 1){
                    expenseType = ExpenseType.EXPENSE;
                }
                double amount = c.getDouble(3);
                Transaction transaction = new Transaction(date,accountNumber,expenseType,amount);
                transactions.add(transaction);
                if(c.moveToNext()){
                    break;
                }
            }
        }
        c.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor c = db.query(PersistentCreateDB.TABLE_TRANSACTIONS, null, null, null, null, null, null, limit + "");
        if(c.moveToFirst()){
            while (true){
                Date date = new Date(c.getLong(0));
                String acc_no = c.getString(1);
                ExpenseType expenseType = ExpenseType.INCOME;
                if (c.getInt(2) == 1) {
                    expenseType = ExpenseType.EXPENSE;
                }
                double amount = c.getDouble(3);
                Transaction transaction = new Transaction(date, acc_no, expenseType, amount);
                transactions.add(transaction);
                if(!c.moveToNext()){
                    break;
                }
            }
        }
        c.close();
        return transactions;
    }
}
