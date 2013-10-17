package ru.nsu.ccfit.malkov.phonebook;

import android.app.Application;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author malkov
 */
public class PhonebookApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
    }
}
