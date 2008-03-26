package net.lunglet.gridgain;

public interface ResultHandler<T> {
    void onResult(T result);
}
