package ru.nsu.ccfit.malkov.phonebook.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import ru.nsu.ccfit.malkov.phonebook.R;
import ru.nsu.ccfit.malkov.phonebook.model.BusHolder;
import ru.nsu.ccfit.malkov.phonebook.model.PhoneContact;
import ru.nsu.ccfit.malkov.phonebook.model.events.CallEvent;

import java.io.File;
import java.util.*;

/**
 * @author malkov
 */
public class ContactListAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {

    private List<PhoneContact> contacts;
    private Context context;

    private SparseBooleanArray selectionIds;
    private List<Character> letters;

    private DisplayImageOptions options;

    public ContactListAdapter(Context context, List<PhoneContact> contacts) {

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .build();

        selectionIds = new SparseBooleanArray();

        Collections.sort(contacts, new Comparator<PhoneContact>() {
            @Override
            public int compare(PhoneContact lhs, PhoneContact rhs) {
                final String left = lhs.getName().toUpperCase();
                final String right = rhs.getName().toUpperCase();

                return left.compareTo(right);
            }
        });

        this.contacts = contacts;
        this.context = context;

        alanyzeContacts();
    }

    private void alanyzeContacts() {
        final Set<Character> setLetters = new HashSet<Character>();
        for (final PhoneContact contact : contacts) {
            if (!TextUtils.isEmpty(contact.getName())) {
                setLetters.add(contact.getName().toUpperCase().charAt(0));
            }
        }

        letters = new ArrayList<Character>(setLetters);

        Collections.sort(letters);
    }

    @Override
    public int getCountForHeader(int header) {
        int count = 0;
        final char first = letters.get(header);
        for (final PhoneContact contact : contacts) {
            if (!TextUtils.isEmpty(contact.getName()) && first == contact.getName().toUpperCase().charAt(0)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getNumHeaders() {
        return letters.size();
    }

    public void removeSelection() {
        selectionIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.header_item_layout, parent, false);
        }

        final TextView header = (TextView)convertView.findViewById(R.id.header_text);

        final char first = letters.get(position);

        final String date = String.valueOf(first);
        if (!TextUtils.equals(date, header.getText())) {
            Log.e("TEST", date);
            header.setText(date);
        }
        return header;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public PhoneContact getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return contacts.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        }

        final PhoneContact contact = contacts.get(position);

        ((TextView)convertView.findViewById(R.id.name)).setText(contact.getName());

        ((TextView)convertView.findViewById(R.id.number)).setText('(' + contact.getNumber() + ')');

        convertView.findViewById(R.id.ring).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BusHolder.getInstance().post(new CallEvent(contact.getNumber()));
            }
        });

        convertView
                .setBackgroundColor(selectionIds.get(position) ? 0x9934B5E4
                        : Color.TRANSPARENT);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

        if (getItem(position).getIcon() != null) {

            ImageLoader.getInstance().displayImage(getItem(position).getIcon(), icon, options, null);
        } else {
            ImageLoader.getInstance().cancelDisplayTask(icon);
        }


        return convertView;
    }

    public void toggleSelection(int position) {
        selectView(position, !selectionIds.get(position));
    }

    private void selectView(int position, boolean b) {
        if (b) {
            selectionIds.put(position, b);
        } else {
            selectionIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public void remove(PhoneContact item) {
        contacts.remove(item);
        notifyDataSetChanged();
    }

    public int getSelectedViewCount() {
        return selectionIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectionIds;
    }
}
