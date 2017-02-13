package com.vlkan.hrrs.serializer;

import com.pholser.junit.quickcheck.From;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import com.vlkan.hrrs.api.*;
import com.vlkan.hrrs.serializer.base64.Base64HttpRequestRecordReader;
import com.vlkan.hrrs.serializer.base64.Base64HttpRequestRecordWriter;
import org.junit.runner.RunWith;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitQuickcheck.class)
public class HttpRequestSerializationTest {

    @Property
    public void should_write_and_read(@From(HttpRequestRecordGenerator.class) HttpRequestRecord record) {
        HttpRequestRecordPipe pipe = new HttpRequestRecordPipe(HttpRequestPayloadGenerator.MAX_BYTE_COUNT * 8);
        HttpRequestRecordReader<String> reader = new Base64HttpRequestRecordReader(pipe);
        HttpRequestRecordWriter<String> writer = new Base64HttpRequestRecordWriter(pipe);
        writer.write(record);
        pipe.flush();
        Iterator<HttpRequestRecord> iterator = reader.read().iterator();
        assertThat(iterator.hasNext()).isTrue();
        HttpRequestRecord readRecord = iterator.next();
        final boolean equals = readRecord.equals(record);
        assertThat(readRecord).isEqualTo(record);
    }

}
