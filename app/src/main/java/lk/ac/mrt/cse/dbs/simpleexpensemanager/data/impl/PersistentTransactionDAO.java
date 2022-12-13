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

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db=database.getWritableDatabase();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersistentCreateDB.KEY_ACCOUNT_NO,accountNo);
        contentValues.put(PersistentCreateDB.KEY_EXPENSE_TYPE,(expenseType.name()));
        contentValues.put(PersistentCreateDB.KEY_AMOUNT,amount);
        contentValues.put(PersistentCreateDB.KEY_DATE,simpleDateFormat.format(date));
        db.insert(PersistentCreateDB.TABLE_TRANSACTIONS,null,contentValues);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();

        Cursor c=database.getWritableDatabase().query(PersistentCreateDB.TABLE_ACCOUNTS,null,null,null,null,null,null);
        //Cursor c = db.query(PersistentCreateDB.TABLE_TRANSACTIONS, null, null, null, null, null, null);
        if(c.moveToFirst()){
            while (true){
                Date date = null;
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(c.getString(1));
                    Transaction transaction = new Transaction(date,c.getString(2),ExpenseType.valueOf(c.getString(3)),Double.parseDouble(c.getString(4)));
                    transactions.add(transaction);
                }catch (Exception e){

                }

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
        List<Transaction> temparr = getAllTransactionLogs();
        int size = temparr.size();
        if (size <= limit) {
            return temparr;
        }
        // return the last <code>limit</code> number of transaction logs
        return temparr.subList(size - limit, size);
    }
}
