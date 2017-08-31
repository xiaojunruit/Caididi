package com.laoodao.caididi.validator;

import android.content.Context;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ezy.lite.util.UI;

public class DefaultValidator {

    private Validator mValidator;
    private OnValidationSucceeded onSuccess;
    private OnValidationFailed onFailed;


    public DefaultValidator() {

    }

    public DefaultValidator(Object o) {
        init(o);
    }

    protected void init(Object o) {
        mValidator = new Validator(o);
        mValidator.setValidationMode(Validator.Mode.IMMEDIATE);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                if (onSuccess == null) {
                    return;
                }
                try {
                    onSuccess.onValidateSuccess();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                if (onSuccess == null) {
                    return;
                }
                Context context = errors.get(0).getView().getContext();
                UI.showToast(context, errors.get(0).getFailedRules().get(0).getMessage(context));
            }
        });

    }

    public void validate() {
        mValidator.validate();
    }

    public <T extends DefaultValidator> T clicked(View view) {
        RxView.clicks(view).throttleFirst(1, TimeUnit.SECONDS).subscribe(v -> mValidator.validate());
        return (T) this;
    }

    public <T extends DefaultValidator> T succeeded(OnValidationSucceeded l) {
        onSuccess = l;
        return (T) this;
    }

    public <T extends DefaultValidator> T failed(OnValidationFailed l) {
        onFailed = l;
        return (T) this;
    }
}