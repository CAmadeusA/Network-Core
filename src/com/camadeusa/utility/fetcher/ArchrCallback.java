package com.camadeusa.utility.fetcher;

import java.util.Map;
import java.util.UUID;

public interface ArchrCallback<T> {
    public void onFetchDone(T result);

}
