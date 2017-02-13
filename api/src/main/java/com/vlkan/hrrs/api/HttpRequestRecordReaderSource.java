package com.vlkan.hrrs.api;

import java.io.Closeable;

public interface HttpRequestRecordReaderSource<T> extends Closeable {

    T read();

}
