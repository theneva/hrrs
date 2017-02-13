package com.vlkan.hrrs.serializer.base64;

import com.vlkan.hrrs.api.HttpRequestRecord;
import com.vlkan.hrrs.api.HttpRequestRecordReader;
import com.vlkan.hrrs.api.HttpRequestRecordReaderSource;

import java.util.Objects;

public class Base64HttpRequestRecordReader implements HttpRequestRecordReader<String> {

    private final HttpRequestRecordReaderSource<String> source;

    public Base64HttpRequestRecordReader(HttpRequestRecordReaderSource<String> source) {
        this.source = Objects.requireNonNull(source, "source");
    }

    @Override
    public HttpRequestRecordReaderSource<String> getSource() {
        return source;
    }

    @Override
    public Iterable<HttpRequestRecord> read() {
        return new Base64HttpRequestRecordReaderIterable(source);
    }

}
