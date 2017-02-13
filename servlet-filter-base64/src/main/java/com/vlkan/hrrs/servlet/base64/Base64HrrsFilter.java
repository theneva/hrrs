package com.vlkan.hrrs.servlet.base64;

import com.vlkan.hrrs.api.HttpRequestRecordWriter;
import com.vlkan.hrrs.api.HttpRequestRecordWriterTarget;
import com.vlkan.hrrs.serializer.base64.Base64HttpRequestRecord;
import com.vlkan.hrrs.serializer.base64.Base64HttpRequestRecordWriter;
import com.vlkan.hrrs.serializer.file.HttpRequestRecordWriterFileTarget;
import com.vlkan.hrrs.servlet.HrrsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Base64HrrsFilter extends HrrsFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Base64HrrsFilter.class);

    private final File writerTargetFile;

    private final HttpRequestRecordWriterTarget<String> writerTarget;

    private final HttpRequestRecordWriter<String> writer;

    public Base64HrrsFilter(File writerTargetFile) {
        this.writerTargetFile = Objects.requireNonNull(writerTargetFile, "writerTargetFile");
        this.writerTarget = new HttpRequestRecordWriterFileTarget(writerTargetFile, Base64HttpRequestRecord.CHARSET);
        this.writer = new Base64HttpRequestRecordWriter(writerTarget);
    }

    public File getWriterTargetFile() {
        return writerTargetFile;
    }

    public HttpRequestRecordWriterTarget<String> getWriterTarget() {
        return writerTarget;
    }

    @Override
    protected HttpRequestRecordWriter<String> getWriter() {
        return writer;
    }

    @Override
    public void destroy() {
        try {
            writerTarget.close();
        } catch (IOException error) {
            String message = String.format("failed closing writer (writerTargetFile=%s)", writerTargetFile);
            LOGGER.error(message, error);
        }
    }

}
