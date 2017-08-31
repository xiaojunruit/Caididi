package com.laoodao.caididi.ui.widget.wheelPicker;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laoodao.caididi.retrofit.main.City;
import com.laoodao.caididi.retrofit.user.MechanicalType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class MechanicalSinglePicker<T extends MechanicalType> extends WheelPicker {
    private List<T> items = new ArrayList<T>();
    private List<String> itemStrings = new ArrayList<String>();
    private WheelView wheelView;
    private OnWheelListener onWheelListener;
    private OnItemPickListener<T> onItemPickListener;
    private int selectedItemIndex = 0;
    private String label = "";

    public MechanicalSinglePicker(Activity activity, T[] items) {
        this(activity, Arrays.asList(items));
    }

    public MechanicalSinglePicker(Activity activity, List<T> items) {
        super(activity);
        setItems(items);
    }

    /**
     * 添加数据项
     */
    public void addItem(T item) {
        items.add(item);
        itemStrings.add(item.name);
    }

    /**
     * 移除数据项
     */
    public void removeItem(T item) {
        items.remove(item);
        itemStrings.remove(item.name);
    }

    /**
     * 设置数据项
     */
    public void setItems(T[] items) {
        setItems(Arrays.asList(items));
    }

    /**
     * 设置数据项
     */
    public void setItems(List<T> items) {
        if (null == items || items.size() == 0) {
            return;
        }
        this.items = items;
        for (T item : items) {
            itemStrings.add(item.name);
        }
        if (null != wheelView) {
            wheelView.setItems(itemStrings, selectedItemIndex);
        }
    }

    /**
     * 设置显示的单位，如身高为cm、体重为kg
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * 设置默认选中的项的索引
     */
    public void setSelectedIndex(int index) {
        if (index >= 0 && index < items.size()) {
            selectedItemIndex = index;
        }
    }

    /**
     * 设置默认选中的项
     */
    public void setSelectedItem(@NonNull T item) {
        setSelectedIndex(itemStrings.indexOf(item.name));
    }

    /**
     * 设置滑动监听器
     */
    public void setOnWheelListener(OnWheelListener onWheelListener) {
        this.onWheelListener = onWheelListener;
    }

    public void setOnItemPickListener(OnItemPickListener<T> listener) {
        this.onItemPickListener = listener;
    }

    @Override
    @NonNull
    protected View makeCenterView() {
        if (items.size() == 0) {
            throw new IllegalArgumentException("please initial items at first, can't be empty");
        }
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        wheelView = new WheelView(activity);
        wheelView.setTextSize(textSize);
        wheelView.setTextColor(textColorNormal, textColorFocus);
        wheelView.setLineConfig(lineConfig);
        wheelView.setOffset(offset);
        wheelView.setCycleDisable(cycleDisable);
        if (TextUtils.isEmpty(label)) {
            layout.addView(wheelView, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        } else {
            layout.addView(wheelView, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            TextView labelView = new TextView(activity);
            labelView.setTextColor(textColorFocus);
            labelView.setTextSize(textSize);
            labelView.setText(label);
            layout.addView(labelView, new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        }
        wheelView.setItems(itemStrings, selectedItemIndex);
        wheelView.setOnWheelListener(new WheelView.OnWheelListener() {
            @Override
            public void onSelected(boolean isUserScroll, int index, String item) {
                selectedItemIndex = index;
                if (onWheelListener != null) {
                    onWheelListener.onWheeled(selectedItemIndex, item);
                }
            }
        });
        return layout;
    }
    @Override
    public void onSubmit() {
        if (onItemPickListener != null) {
            onItemPickListener.onItemPicked(selectedItemIndex, getSelectedItem());
        }
    }

    public T getSelectedItem() {
        return items.get(selectedItemIndex);
    }

    public int getSelectedIndex() {
        return selectedItemIndex;
    }

    public interface OnItemPickListener<T> {

        void onItemPicked(int index, T item);

    }

    public interface OnWheelListener {

        void onWheeled(int index, String item);

    }

}