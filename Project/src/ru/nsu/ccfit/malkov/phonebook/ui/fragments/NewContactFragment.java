package ru.nsu.ccfit.malkov.phonebook.ui.fragments;

import android.animation.Animator;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import ru.nsu.ccfit.malkov.phonebook.R;
import ru.nsu.ccfit.malkov.phonebook.db.ContactOpenHelper;
import ru.nsu.ccfit.malkov.phonebook.db.DaoHolder;
import ru.nsu.ccfit.malkov.phonebook.model.PhoneContact;

import java.sql.SQLException;

/**
 * @author malkov
 */
public class NewContactFragment extends Fragment {

    public static final int REQUEST_CODE_GET_ICON = 12334;
    public static final String SHOWING_FRAGMENT_EXTRA = "showing_fragment_extra_bundle";

    private EditText name;
    private EditText number;
    private EditText email;

    private ImageView icon;
    private Uri iconUri = null;

    private PhoneContact current = null;

    public static NewContactFragment newInstance(PhoneContact contact) {
        final Bundle bundle = new Bundle();

        bundle.putSerializable(SHOWING_FRAGMENT_EXTRA, contact);

        final NewContactFragment fragment = new NewContactFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        final View inflate = inflater.inflate(R.layout.new_contact_layout, container, false);

        name = (EditText) inflate.findViewById(R.id.name);
        number = (EditText) inflate.findViewById(R.id.number);
        email = (EditText) inflate.findViewById(R.id.email);

        icon = (ImageView) inflate.findViewById(R.id.icon);

        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select application"), REQUEST_CODE_GET_ICON);
            }
        });

        if (getArguments() != null && getArguments().containsKey(SHOWING_FRAGMENT_EXTRA)) {
            current = (PhoneContact) getArguments().getSerializable(SHOWING_FRAGMENT_EXTRA);

            name.setText(current.getName());
            number.setText(current.getNumber());
            email.setText(current.getEmail());

            if (current.getIcon() != null) {
                ImageLoader.getInstance().displayImage(current.getIcon(), icon);
            } else {
                ImageLoader.getInstance().cancelDisplayTask(icon);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == OrmLiteBaseActivity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GET_ICON) {
                iconUri = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getActivity().getContentResolver().query(iconUri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                icon.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_contact_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.save) {
            saveAndExit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAndExit() {
        try {
            if (!TextUtils.isEmpty(name.getText().toString())) {
                if (current != null) {
                    current.setName(name.getText().toString());
                    current.setNumber(number.getText().toString());
                    current.setEmail(email.getText().toString());
                    if (iconUri != null) {
                        current.setIcon(iconUri.toString());
                    }
                } else {
                    current = new PhoneContact(name.getText().toString(), iconUri.toString(), number.getText().toString(), email.getText().toString());
                }
                DaoHolder.getDefaultDao(getActivity()).
                        createOrUpdate(current);

                getActivity().onBackPressed();
            } else {
                Toast.makeText(getActivity(), R.string.fill_name, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            Log.e(NewContactFragment.class.getSimpleName(), e.getLocalizedMessage(), e);
            Toast.makeText(getActivity(), R.string.unable_to_save, Toast.LENGTH_SHORT).show();
        }
    }
}
