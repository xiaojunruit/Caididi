package com.laoodao.caididi.ui.widget.wheelPicker;

import android.app.Activity;

import com.laoodao.caididi.retrofit.user.MechanicalType;

import java.util.List;

/**
 */
public class MechanicalOptionPicker extends MechanicalSinglePicker<MechanicalType> {

    public MechanicalOptionPicker(Activity activity, List<MechanicalType> items) {
        super(activity, items);
    }

    public void setOnOptionPickListener(OnOptionPickListener listener) {
        super.setOnItemPickListener(listener);
    }

    public static abstract class OnOptionPickListener implements OnItemPickListener<MechanicalType> {

        public abstract void onOptionPicked(int index, MechanicalType item);

        @Override
        public final void onItemPicked(int index, MechanicalType item) {
            onOptionPicked(index, item);
        }

    }

}
