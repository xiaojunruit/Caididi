
package com.laoodao.caididi.common.util;

import android.app.Activity;
import android.app.ProgressDialog;

import rx.Observable;
import rx.Subscriber;

public class ProgressOperator<T> implements Observable.Operator<T, T> {

    private ProgressDialog mProgressDialog;
    Activity mContext;
    String mText;
    public ProgressOperator(Activity context, String text) {
        mContext = context;
        mText = text;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> s) {
        return new Subscriber<T>(s) {
            @Override
            public void onStart() {
                if (mContext == null || mContext.isFinishing()) {
                    return;
                }
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setMessage(mText);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setOnCancelListener(dialog -> {
                    s.unsubscribe();
                    mProgressDialog = null;
                });
                    mProgressDialog.show();
            }
            private void hide() {
                if(mProgressDialog != null) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }

            @Override
            public void onCompleted() {
                hide();
                if (!s.isUnsubscribed()) {
                    s.onCompleted();
                }
            }

            @Override
            public void onError(Throwable t) {
                hide();
                if (!s.isUnsubscribed()) {
                    s.onError(t);
                }
            }

            @Override
            public void onNext(T item) {
                if (!s.isUnsubscribed()) {
                    s.onNext(item);
                }
            }
        };
    }
}