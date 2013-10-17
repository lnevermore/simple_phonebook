package ru.nsu.ccfit.malkov.phonebook.ui.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import ru.nsu.ccfit.malkov.phonebook.R;
import ru.nsu.ccfit.malkov.phonebook.db.ContactOpenHelper;
import ru.nsu.ccfit.malkov.phonebook.ui.fragments.ContactListFragment;

public class MainContactsBook extends OrmLiteBaseActivity<ContactOpenHelper> {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.root);

        getActionBar().setDisplayHomeAsUpEnabled(false);

        startApp();

    }

    private void startApp() {
        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();


        final ContactListFragment contactListFragment = new ContactListFragment();

        contactListFragment.setHasOptionsMenu(true);

        fragmentTransaction.add(R.id.root, contactListFragment, ContactListFragment.class.getSimpleName());

        fragmentTransaction.commit();
    }
}
