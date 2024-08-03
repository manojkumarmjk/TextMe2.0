package com.bymjk.txtme.Components;

public interface GenericCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}