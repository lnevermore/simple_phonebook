package ru.nsu.ccfit.malkov.phonebook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ru.nsu.ccfit.malkov.phonebook.model.PhoneContact;

import java.sql.SQLException;

/**
 * @author malkov
 */
public class ContactOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final String NAME_DB = "malkov_contacts.sqlite";
    private static final int VERSION = 1;
    private Dao<PhoneContact, Integer> simpleDao;

    public ContactOpenHelper(Context context) {
        super(context, NAME_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, PhoneContact.class);
        } catch (SQLException e) {
            Log.e(ContactOpenHelper.class.getSimpleName(), e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }


        try {
            getDao().create(new PhoneContact("Yasha Reshetnikova", null, "8-923-112-24-42", "yasha@gmail.com"));
            getDao().create(new PhoneContact("Matvey Malkov", null, "8-923-176-62-32", "matvey@gmail.com"));
            getDao().create(new PhoneContact("Mirosha", null, "8-923-992-23-42", "miron@gmail.com"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
    }

    public Dao<PhoneContact, Integer> getDao() throws SQLException {
        if(simpleDao == null) {
            simpleDao = getDao(PhoneContact.class);
        }

        return simpleDao;
    }

    @Override
    public void close() {
        super.close();
        simpleDao = null;
    }
}
