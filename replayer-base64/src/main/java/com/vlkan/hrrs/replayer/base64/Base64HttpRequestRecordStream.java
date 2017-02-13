package com.vlkan.hrrs.replayer.base64;

import com.vlkan.hrrs.api.HttpRequestRecord;
import com.vlkan.hrrs.api.HttpRequestRecordReader;
import com.vlkan.hrrs.api.HttpRequestRecordReaderSource;
import com.vlkan.hrrs.replayer.record.HttpRequestRecordStream;
import com.vlkan.hrrs.replayer.record.HttpRequestRecordStreamConsumer;
import com.vlkan.hrrs.serializer.base64.Base64HttpRequestRecord;
import com.vlkan.hrrs.serializer.base64.Base64HttpRequestRecordReader;
import com.vlkan.hrrs.serializer.file.HttpRequestRecordReaderFileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Callable;

import static com.vlkan.hrrs.replayer.util.Throwables.throwCheckedException;

public class Base64HttpRequestRecordStream implements HttpRequestRecordStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(Base64HttpRequestRecordStream.class);

    @Override
    public void consumeWhile(URI inputUri, Callable<Boolean> predicate, HttpRequestRecordStreamConsumer consumer) {
        Objects.requireNonNull(inputUri, "inputUri");
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(consumer, "consumer");
        LOGGER.debug("consuming (inputUri={})", inputUri);
        boolean resuming = false;
        do {
            File inputFile = new File(inputUri);
            HttpRequestRecordReaderSource<String> readerSource =
                    new HttpRequestRecordReaderFileSource(inputFile, Base64HttpRequestRecord.CHARSET);
            try {
                HttpRequestRecordReader<String> reader = new Base64HttpRequestRecordReader(readerSource);
                Iterator<HttpRequestRecord> iterator = reader.read().iterator();
                while ((resuming = predicate.call()) && iterator.hasNext()) {
                    HttpRequestRecord record = iterator.next();
                    consumer.consume(record);
                }
            } catch (Exception error) {
                throwCheckedException(error);
            } finally {
                try {
                    readerSource.close();
                } catch (IOException error) {
                    throwCheckedException(error);
                }
            }
        } while (resuming);
    }

}
