package ru.nsu.ccfit.malkov.phonebook.db;

import android.content.Context;
import android.util.Log;
import com.j256.ormlite.dao.Dao;
import ru.nsu.ccfit.malkov.phonebook.model.PhoneContact;

import java.sql.SQLException;

/**
 * @author malkov
 */
public class DaoHolder {

    static ContactOpenHelper helper;

    public static Dao<PhoneContact, Integer> getDefaultDao(Context context) {
        try {
            if (helper == null) {
                helper = new ContactOpenHelper(context);
            }
            return helper.getDao();
        } catch (SQLException e) {
            Log.e(DaoHolder.class.getSimpleName(), e.getLocalizedMessage(), e);
            throw new RuntimeException("Can't get dao", e);
        }
    }
}
