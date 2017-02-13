package com.vlkan.hrrs.serializer.base64;

import com.vlkan.hrrs.api.HttpRequestRecord;
import com.vlkan.hrrs.api.HttpRequestRecordReaderSource;

import java.util.Iterator;
import java.util.Objects;

public class Base64HttpRequestRecordReaderIterable implements Iterable<HttpRequestRecord> {

    private final HttpRequestRecordReaderSource<String> source;

    Base64HttpRequestRecordReaderIterable(HttpRequestRecordReaderSource<String> source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    @Override
    public Iterator<HttpRequestRecord> iterator() {
        return new Base64HttpRequestRecordReaderIterator(source);
    }
}
