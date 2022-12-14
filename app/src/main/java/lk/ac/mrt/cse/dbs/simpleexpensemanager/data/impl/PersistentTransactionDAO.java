package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private PersistentCreateDB database;
    public PersistentTransactionDAO(PersistentCreateDB database){
        this.database = database;
    }

    //entering values to the database
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db=database.getWritableDatabase();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ContentValues contentValues = new ContentValues();
        //values of the columns
        contentValues.put(PersistentCreateDB.KEY_ACCOUNT_NO,accountNo);
        contentValues.put(PersistentCreateDB.KEY_EXPENSE_TYPE,(expenseType == ExpenseType.INCOME)?0:1);
        contentValues.put(PersistentCreateDB.KEY_AMOUNT,amount);
        contentValues.put(PersistentCreateDB.KEY_DATE,simpleDateFormat.format(date));

        //inserting values to the transaction table
        db.insert(PersistentCreateDB.TABLE_TRANSACTIONS,null,contentValues);
        db.close();
    }


    //reading database to output values in the app
    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();
        //String query = "SELECT * from transaction_log";
        SQLiteDatabase db = database.getReadableDatabase();

        //reading the database
        Cursor c = db.query(PersistentCreateDB.TABLE_TRANSACTIONS, null, null, null, null, null, null);
        if(c.moveToFirst()){
            while (true){
                String dt = c.getString(1);
                Date date = new Date();
                try {
                    date = (new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())).parse(dt);
                }catch (Exception e){
                }
                String accountNumber = c.getString(2);
                ExpenseType expenseType = ExpenseType.INCOME;
                if(c.getInt(3) == 1){
                    expenseType = ExpenseType.EXPENSE;
                }
                double amount = c.getDouble(4);

                //creating transaction objects
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


    //just print the latest transaction values in app
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor c = db.query(PersistentCreateDB.TABLE_TRANSACTIONS, null, null, null, null, null, null, null);
        if(c.moveToFirst()){
            while (true){
                String dt = c.getString(1);
                Date date = new Date();
                try {
                    date = (new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())).parse(dt);
                }catch (Exception e){
                }
                String acc_no = c.getString(2);
                ExpenseType expenseType = ExpenseType.INCOME;
                if (c.getInt(3) == 1) {
                    expenseType = ExpenseType.EXPENSE;
                }
                double amount = c.getDouble(4);
                Transaction transaction = new Transaction(date, acc_no, expenseType, amount);
                transactions.add(transaction);
                if(!c.moveToNext()){
                    break;
                }
            }
        }
        c.close();
        if (transactions.size() <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(transactions.size() - limit, transactions.size());
    }
}
