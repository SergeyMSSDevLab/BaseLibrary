package com.mssdevlab.baselib.common;

public class Event<T> {
    private boolean hasBeenHandled = false;
    private T value;

    public Event(T value) {
        this.value = value;
    }

    public T getValueIfNotHandled(){
        if (hasBeenHandled){
            return null;
        } else {
            hasBeenHandled = true;
            return this.value;
        }
    }

    public T peekValue(){
        return this.value;
    }
}
