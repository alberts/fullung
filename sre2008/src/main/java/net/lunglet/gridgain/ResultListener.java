package net.lunglet.gridgain;

import java.util.EventListener;

public interface ResultListener<T> extends EventListener {
    void onResult(T result);
}
