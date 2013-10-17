package ru.nsu.ccfit.malkov.phonebook.ui.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.squareup.otto.Subscribe;
import ru.nsu.ccfit.malkov.phonebook.R;
import ru.nsu.ccfit.malkov.phonebook.db.DaoHolder;
import ru.nsu.ccfit.malkov.phonebook.model.BusHolder;
import ru.nsu.ccfit.malkov.phonebook.model.PhoneContact;
import ru.nsu.ccfit.malkov.phonebook.model.events.CallEvent;
import ru.nsu.ccfit.malkov.phonebook.ui.adapters.ContactListAdapter;
import ru.nsu.ccfit.malkov.phonebook.ui.dialogs.ShowContactFragment;

import java.sql.SQLException;
import java.util.List;

/**
 * @author malkov
 */
public class ContactListFragment extends Fragment {

    private GridView list;

    private ActionMode actionMode;
    private ContactListAdapter contactListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.main, container, false);


        BusHolder.getInstance().register(this);
        list = (GridView) inflate.findViewById(R.id.listview);
        list.setEmptyView(inflate.findViewById(R.id.empty_view));




        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            List<PhoneContact> phoneContacts = DaoHolder.getDefaultDao(getActivity()).queryForAll();

            contactListAdapter = new ContactListAdapter(getActivity(), phoneContacts);

            list.setAdapter(contactListAdapter);
        } catch (SQLException e) {
            Log.e(ContactListFragment.class.getSimpleName(), e.getLocalizedMessage(), e);
        }

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (actionMode == null) {
            /*no items selected, so perform item click actions
             * like moving to next activity */
                    final ShowContactFragment fragment = ShowContactFragment.newFragmentInstance(contactListAdapter.getItem(position));

                    final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

                    fragment.setHasOptionsMenu(true);
                    fragmentTransaction.replace(R.id.root, fragment, ShowContactFragment.class.getSimpleName());

                    fragmentTransaction.addToBackStack(ShowContactFragment.class.getSimpleName());
                    fragmentTransaction.commit();


                } else
                    // add or remove selection for current list item
                    onListItemSelect(position);

            }
        });
    }

    private void onListItemSelect(int position) {
        contactListAdapter.toggleSelection(position);
        boolean hasCheckedItems = contactListAdapter.getSelectedViewCount() > 0;

        if (hasCheckedItems && actionMode == null)
            // there are some selected items, start the actionMode
            actionMode = getActivity().startActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && actionMode != null)
            // there no selected items, finish the actionMode
            actionMode.finish();

        if (actionMode != null)
            actionMode.setTitle(String.valueOf(contactListAdapter
                    .getSelectedViewCount()) + " selected");
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_new) {
            addNewContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addNewContact() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        final NewContactFragment newContactFragment = new NewContactFragment();

        newContactFragment.setHasOptionsMenu(true);

        fragmentTransaction.replace(R.id.root, newContactFragment, NewContactFragment.class.getSimpleName());
        fragmentTransaction.addToBackStack(NewContactFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Subscribe public void call(CallEvent event) {
        final Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + event.getNumber()));
        startActivity(intent);
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.deletion_context_menu, menu);
            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                case R.id.delete_item:
                    // retrieve selected items and delete them out
                    SparseBooleanArray selected = contactListAdapter
                            .getSelectedIds();

                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            PhoneContact selectedItem = contactListAdapter
                                    .getItem(selected.keyAt(i));
                            try {
                                DaoHolder.getDefaultDao(getActivity()).delete(selectedItem);
                                contactListAdapter.remove(selectedItem);
                            } catch (SQLException e) {
                                Log.e(ContactListFragment.class.getSimpleName(), e.getLocalizedMessage(), e);
                                Toast.makeText(getActivity(), R.string.cant_remove, Toast.LENGTH_SHORT).show();
                            }
                            //contactListAdapter.(selectedItem);
                        }
                    }
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            contactListAdapter.removeSelection();
            actionMode = null;
        }
    }
}
