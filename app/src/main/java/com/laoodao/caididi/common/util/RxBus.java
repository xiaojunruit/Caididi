package com.laoodao.caididi.common.util;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxBus {

    private static final Subject bus = new SerializedSubject<>(PublishSubject.create());

    public static void post (Object o) {
        bus.onNext(o);
    }
    public static<T extends Object> Observable<T> ofType (Class<T> clazz) {
        return bus.ofType(clazz);
    }
}