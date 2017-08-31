package com.laoodao.caididi.ui.widget.wheelPicker;

import android.app.Activity;

import com.laoodao.caididi.retrofit.main.City;

import java.util.List;

/**
 */
public class OptionPicker extends SinglePicker<City> {

    public OptionPicker(Activity activity, List<City> items) {
        super(activity, items);
    }

    public void setOnOptionPickListener(OnOptionPickListener listener) {
        super.setOnItemPickListener(listener);
    }

    public static abstract class OnOptionPickListener implements OnItemPickListener<City> {

        public abstract void onOptionPicked(int index, City item);

        @Override
        public final void onItemPicked(int index, City item) {
            onOptionPicked(index, item);
        }

    }

}
