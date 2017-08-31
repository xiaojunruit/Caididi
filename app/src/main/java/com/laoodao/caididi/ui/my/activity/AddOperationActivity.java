package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.KeyboardUtil;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityAddOperateBinding;
import com.laoodao.caididi.databinding.ActivityAddOperationBinding;
import com.laoodao.caididi.databinding.ItemAddFarmlandBinding;
import com.laoodao.caididi.databinding.ItemAddMachineBinding;
import com.laoodao.caididi.event.CloseOperation;
import com.laoodao.caididi.event.FarmlandOperateEvent;
import com.laoodao.caididi.retrofit.user.MechanicalType;
import com.laoodao.caididi.retrofit.user.OpDetail;
import com.laoodao.caididi.ui.widget.wheelPicker.DatePicker;
import com.laoodao.caididi.ui.widget.wheelPicker.MechanicalOptionPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;

/**
 * Created by WORK on 2017/2/15.
 */

public class AddOperationActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddOperationBinding binding;
    private String[] cropsName;
    private String[] cropsMoney;
    private String[] machineName;
    private String[] machineMoney;
    private int mId;
    private String mOperate;
    private int mFarmlandId;
    private int mOpearteId = 0;
    private final int OTHER_ID = 13;
    private LayoutInflater mInflater;

    public static void start(Context context, int farmlandId) {
        Bundle bundle = new Bundle();
        bundle.putInt("farmlandId", farmlandId);
        ContextUtil.startActivity(context, AddOperationActivity.class, bundle);
    }

    public static void start(Context context, int id, String operate, int farmlandId) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("operate", operate);
        bundle.putInt("farmlandId", farmlandId);
        ContextUtil.startActivity(context, AddOperationActivity.class, bundle);
    }

    public static void start(Context context, int opearteId, int farmlandId, String operate) {
        Bundle bundle = new Bundle();
        bundle.putInt("opearteId", opearteId);
        bundle.putString("operate", operate);
        bundle.putInt("farmlandId", farmlandId);
        ContextUtil.startActivity(context, AddOperationActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_operation);
        binding.setListener(this);
        mOpearteId = getIntent().getIntExtra("opearteId", 0);
        mFarmlandId = getIntent().getIntExtra("farmlandId", 0);
        mInflater = LayoutInflater.from(this);
        mOperate = getIntent().getStringExtra("operate");
        binding.setOperate(mOperate);
        binding.getRoot().postDelayed(() -> {
            KeyboardUtil.hideSoftKeyboard(binding.getRoot());
        }, 1);
        if (mOpearteId > 0) {
            initData();
            return;
        }
        mId = getIntent().getIntExtra("id", 0);
        binding.flOther.setVisibility(mId == OTHER_ID ? View.VISIBLE : View.GONE);
        initViews();
        initDate();
        RxBus.ofType(FarmlandOperateEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            mId = event.id;
            mOperate = event.name;
            mFarmlandId = event.farmlandId;
            binding.setOperate(mOperate);
        });
    }

    private void initData() {
        API.user().opDetail(mOpearteId).compose(transform()).subscribe(result -> {
            OpDetail opDetail = result.data;
            for (OpDetail.CapitalCost cost : opDetail.capitalCostList) {
                addFarming(cost);
            }
            for (OpDetail.MachineryCost mc : opDetail.machineryCostList) {
                LogUtil.e("mmmmmmmmm" + mc.mathineName);
                addMachine(mc);
            }
            binding.etMoeny.setText(opDetail.artificial);
            binding.etNote.setText(opDetail.remark);
            binding.txtTime.setText(opDetail.time);
            binding.txtOperationName.setText(opDetail.typeName);

        });
    }

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private void initDate() {

        binding.txtTime.setText(df.format(new Date()));
    }

    private void initViews() {
        addFarming(null);
        addMachine(null);
    }

    private void addMachine(OpDetail.MachineryCost mc) {
        ItemAddMachineBinding machineBinding = DataBindingUtil.inflate(mInflater, R.layout.item_add_machine, binding.llMachine, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = Device.dp2px(10);
        machineBinding.getRoot().setLayoutParams(params);
        machineBinding.flType.setOnClickListener(v -> {
            API.user().getMachine().compose(transform()).subscribe(result -> {
                cityDialog(result.data, machineBinding.txtMachineType);
            });
        });
        if (mc != null) {
            machineBinding.txtMachineType.setText(mc.mathineName);
            machineBinding.etMoney.setText(mc.mathineMoney);
            machineBinding.txtMachineType.setTag(mc.id);
        }
        machineBinding.imgMachine.setOnClickListener(v -> {
            binding.llMachine.removeView(machineBinding.getRoot());
        });
        binding.llMachine.addView(machineBinding.getRoot());
        machineBinding.imgMachine.setVisibility(binding.llMachine.getChildCount() == 1 ? View.GONE : View.VISIBLE);

    }

    private void addFarming(OpDetail.CapitalCost cost) {

        ItemAddFarmlandBinding farmlandBinding = DataBindingUtil.inflate(mInflater, R.layout.item_add_farmland, binding.llFarming, false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = Device.dp2px(10);
        farmlandBinding.getRoot().setLayoutParams(params);
        if (cost != null) {
            farmlandBinding.edittxtType.setText(cost.cropsName);
            farmlandBinding.etMoney.setText(cost.cropsMoney);
        }

        farmlandBinding.imgFarmland.setOnClickListener(v -> {
            binding.llFarming.removeView(farmlandBinding.getRoot());
        });
        binding.llFarming.addView(farmlandBinding.getRoot());
        farmlandBinding.imgFarmland.setVisibility(binding.llFarming.getChildCount() == 1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_complete:
                submit();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_farming:
                addFarming(null);
                break;
            case R.id.btn_add_machine:
                addMachine(null);
                break;
            case R.id.txt_time:
                try {
                    onYearMonthDayPicker(df.parse(binding.txtTime.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_select:
                FarmlandOperateActivity.start(this, mFarmlandId);
                break;
        }
    }


    public void onYearMonthDayPicker(Date date) {
        final DatePicker picker = new DatePicker(this);
        picker.setTopPadding(2);
        picker.setRangeStart(1990, 1, 1);
        picker.setRangeEnd(2111, 12, 12);
        // 获取日期实例
        Calendar calendar = Calendar.getInstance();
        // 将日历设置为指定的时间
        calendar.setTime(date);
        // 获取年
        int year = calendar.get(Calendar.YEAR);
        // 这里要注意，月份是从0开始。
        int month = calendar.get(Calendar.MONTH) + 1;
        // 获取天
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        picker.setSelectedItem(year, month, day);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {

                binding.txtTime.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    int error = 0;

    private void submit() {

        String money = binding.etMoeny.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();
        String other = binding.etOperation.getText().toString().trim();
        if (mId == OTHER_ID && TextUtils.isEmpty(other)) {
            error++;
        }
        if (TextUtils.isEmpty(money) || TextUtils.isEmpty(note) || TextUtils.isEmpty(mOperate)) {
            error++;
        }
        getFarming();
        getMachine();

        if (error > 0) {
            error = 0;
            UI.showToast(this, "请填写完整信息");
            return;
        }

        API.user().addOperate(cropsName, cropsMoney, machineName, machineMoney, mFarmlandId, mId, other,
                note, money, binding.txtTime.getText().toString(),
                mOpearteId == 0 ? null : mOpearteId + "")
                .compose(transform()).subscribe(result -> {
            RxBus.post(new CloseOperation());
            finish();
        });

    }

    private void getMachine() {
        int count = binding.llMachine.getChildCount();
        machineName = new String[count];
        machineMoney = new String[count];
        for (int i = 0; i < count; i++) {
            View item = binding.llMachine.getChildAt(i);
            TextView type = (TextView) item.findViewById(R.id.txt_machine_type);
            EditText moeny = (EditText) item.findViewById(R.id.et_money);
            machineName[i] = type.getText() + "_" + type.getTag();
            machineMoney[i] = moeny.getText().toString();
            if (TextUtils.isEmpty(type.getText()) || TextUtils.isEmpty(machineMoney[i])) {
                error++;
            }
        }
    }

    private void getFarming() {
        int count = binding.llFarming.getChildCount();
        cropsName = new String[count];
        cropsMoney = new String[count];

        for (int i = 0; i < count; i++) {
            View item = binding.llFarming.getChildAt(i);
            EditText type = (EditText) item.findViewById(R.id.edittxt_type);
            EditText moeny = (EditText) item.findViewById(R.id.et_money);

            cropsName[i] = type.getText().toString();
            cropsMoney[i] = moeny.getText().toString();

            if (TextUtils.isEmpty(cropsName[i]) || TextUtils.isEmpty(cropsName[i])) {
                error++;
            }
        }

    }

    public void cityDialog(List<MechanicalType> list, TextView textView) {
        MechanicalOptionPicker picker = new MechanicalOptionPicker(this, list);
        picker.setOffset(3);
        picker.setSelectedIndex(0);
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setTopBackgroundColor(0xFFf5f5f5);
        picker.setCancelTextColor(0xff999999);
        picker.setSubmitTextColor(0xff2AB80E);
        picker.setOnOptionPickListener(new MechanicalOptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, MechanicalType itemType) {
                textView.setText(itemType.name);
                textView.setTag(itemType.id);
            }
        });
        picker.show();
    }
}
