package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private PersistentCreateDB database;

    public PersistentAccountDAO(PersistentCreateDB database) {
        this.database=database;
    }
    @Override
    public List<String> getAccountNumbersList() {
        List<String> acc_no = new ArrayList<>();
        SQLiteDatabase db=database.getReadableDatabase();
        Cursor c = db.query(PersistentCreateDB.TABLE_ACCOUNTS, new String[] {database.KEY_ACCOUNT_NO},null,null,null,null,null);
        if(c.moveToFirst()){
            do{acc_no.add(c.getString(0));
            }while(c.moveToNext());
        }
        c.close();
        return acc_no;
    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> acc_list = new ArrayList<>();
        SQLiteDatabase db=database.getReadableDatabase();
        Cursor c = db.query(PersistentCreateDB.TABLE_ACCOUNTS, null,null,null,null,null,null);
        if(c.moveToFirst()){
            do{acc_list.add(new Account(c.getString(0),c.getString(1),c.getString(2),c.getDouble(3)));
            }while(c.moveToNext());
        }
        c.close();
        return acc_list;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account acc;
        SQLiteDatabase db=database.getReadableDatabase();
        Cursor c = db.query(PersistentCreateDB.TABLE_ACCOUNTS, null, PersistentCreateDB.KEY_ACCOUNT_NO + "=?",
                new String[] { accountNo }, null, null, null,null);
        if (c != null) {
            c.moveToFirst();

        }
        acc = new Account(c.getString(0), c.getString(1), c.getString(2), c.getDouble(3));

        c.close();
        return acc;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db=database.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersistentCreateDB.KEY_ACCOUNT_NO, account.getAccountNo());
        contentValues.put(PersistentCreateDB.KEY_BANK_NAME, account.getBankName());
        contentValues.put(PersistentCreateDB.KEY_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        contentValues.put(PersistentCreateDB.KEY_BALANCE, account.getBalance());
        // Inserting Row
        db.insert(PersistentCreateDB.TABLE_ACCOUNTS, null, contentValues);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db=database.getWritableDatabase();
        db.delete(PersistentCreateDB.TABLE_ACCOUNTS, PersistentCreateDB.KEY_ACCOUNT_NO + " = ?", new String[] { accountNo });
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db=database.getWritableDatabase();
        Account acc = this.getAccount(accountNo);
        Cursor c = db.query("account", new String[]{"accountNo", "bankName", "accountHolderName", "balance"}, "accountNo =?", new String[]{accountNo}, null, null, null);
        if (c != null) {
            c.moveToFirst();

        }

        double balance = c.getDouble(3);

        ContentValues contentValues = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                contentValues.put(PersistentCreateDB.KEY_BALANCE, acc.getBalance() - amount);
                break;
            case INCOME:
                contentValues.put(PersistentCreateDB.KEY_BALANCE, acc.getBalance() + amount);
                break;
        }
        db.update(PersistentCreateDB.TABLE_ACCOUNTS, contentValues, PersistentCreateDB.KEY_ACCOUNT_NO + " = ?",
                new String[] { accountNo });

        c.close();
    }
}
