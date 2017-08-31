package com.laoodao.caididi.validator;

import com.mobsandgeeks.saripaar.ValidationError;

import java.util.List;

/**
 * Created by ezy on 15/11/21.
 */
public interface OnValidationFailed {
    void onValidationFailed(List<ValidationError> errors);
}
