package ru.nsu.ccfit.malkov.phonebook.ui.dialogs;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import ru.nsu.ccfit.malkov.phonebook.R;
import ru.nsu.ccfit.malkov.phonebook.model.PhoneContact;
import ru.nsu.ccfit.malkov.phonebook.ui.fragments.NewContactFragment;

/**
 * @author malkov
 */
public class ShowContactFragment extends Fragment {

    private static final String CONTACT_TO_SHOW = "contact_to_show";

    private DisplayImageOptions options;

    public static ShowContactFragment newFragmentInstance(PhoneContact contact) {
        final Bundle bundle = new Bundle();

        bundle.putSerializable(CONTACT_TO_SHOW, contact);

        final ShowContactFragment fragment = new ShowContactFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        return inflater.inflate(R.layout.show_contact_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final PhoneContact contact = (PhoneContact) getArguments().getSerializable(CONTACT_TO_SHOW);

        ((TextView)view.findViewById(R.id.name)).setText(contact.getName());
        ((TextView)view.findViewById(R.id.email)).setText(contact.getEmail());
        ((TextView)view.findViewById(R.id.number)).setText(contact.getNumber());

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        if (contact.getIcon() != null) {
            ImageLoader.getInstance().displayImage(contact.getIcon(), icon, options, null);
        } else {
            ImageLoader.getInstance().cancelDisplayTask(icon);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.show_diaog_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            openEditor();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openEditor() {
        final NewContactFragment fragment = NewContactFragment.newInstance((PhoneContact) getArguments().getSerializable(CONTACT_TO_SHOW));

        fragment.setHasOptionsMenu(true);

        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.root, fragment, NewContactFragment.class.getSimpleName());
        fragmentTransaction.addToBackStack(NewContactFragment.class.getSimpleName());

        fragmentTransaction.commit();
    }
}
