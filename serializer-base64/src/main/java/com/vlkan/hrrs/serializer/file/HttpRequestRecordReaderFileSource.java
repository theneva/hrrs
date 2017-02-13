package com.vlkan.hrrs.serializer.file;

import com.vlkan.hrrs.api.HttpRequestRecordReaderSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class HttpRequestRecordReaderFileSource implements HttpRequestRecordReaderSource<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestRecordReaderFileSource.class);

    private final File file;

    private final Charset charset;

    private final BufferedReader reader;

    public HttpRequestRecordReaderFileSource(File file, Charset charset) {
        this.file = Objects.requireNonNull(file, "file");
        this.charset = Objects.requireNonNull(charset, "charset");
        this.reader = createReader(file, charset);
        LOGGER.trace("instantiated (file={}, charset={})", file, charset);
    }

    private static BufferedReader createReader(File file, Charset charset) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                InputStream readerInputStream = isGzipped(file)
                        ? new GZIPInputStream(fileInputStream)
                        : fileInputStream;
                InputStreamReader inputStreamReader = new InputStreamReader(readerInputStream, charset);
                return new BufferedReader(inputStreamReader);
            } catch (IOException error) {
                fileInputStream.close();
                throw error;
            }
        } catch (IOException error) {
            String message = String.format("failed opening file (file=%s, charset=%s)", file, charset);
            throw new RuntimeException(message, error);
        }
    }

    private static boolean isGzipped(File file) {
        return file.getAbsolutePath().matches(".*\\.[gG][zZ]$");
    }

    public File getFile() {
        return file;
    }

    public Charset getCharset() {
        return charset;
    }

    @Override
    public String read() {
        try {
            return reader.readLine();
        } catch (IOException error) {
            String message = String.format("failed reading line (file=%s)", file);
            throw new RuntimeException(message, error);
        }
    }

    @Override
    public void close() throws IOException {
        LOGGER.trace("closing");
        reader.close();
    }

    @Override
    public String toString() {
        return String.format(
                "HttpRequestRecordReaderFileSource{file=%s, charset=%s}",
                file,
                charset
        );
    }
}
