package com.vlkan.hrrs.replayer.http;

import com.vlkan.hrrs.replayer.cli.Config;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

@Singleton
public class ApacheHttpClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpClientFactory.class);

    private final Config config;

    @Inject
    public ApacheHttpClientFactory(Config config) {
        this.config = Objects.requireNonNull(config, "config");
        LOGGER.debug(
                "instantiated (threadCount={}, timeoutSeconds={}, localAddress={})",
                config.getThreadCount(), config.getRequestTimeoutSeconds(), config.getLocalAddress());
    }

    public CloseableHttpClient create() {
        return HttpClients
                .custom()
                .setDefaultRequestConfig(createRequestConfig())
                .setConnectionManager(createConnectionManager())
                .build();
    }

    private RequestConfig createRequestConfig() {
        int requestTimeoutMillis = (int) (config.getRequestTimeoutSeconds() * 1000);
        RequestConfig.Builder builder = RequestConfig
                .custom()
                .setConnectTimeout(requestTimeoutMillis)
                .setConnectionRequestTimeout(requestTimeoutMillis)
                .setSocketTimeout(requestTimeoutMillis);
        setLocalAddress(builder);
        return builder.build();
    }

    private void setLocalAddress(RequestConfig.Builder builder) {
        String localAddress = config.getLocalAddress();
        if (localAddress != null) {
            try {
                InetAddress localInetAddress = InetAddress.getByName(localAddress);
                builder.setLocalAddress(localInetAddress);
            } catch (UnknownHostException error) {
                String message = String.format("failed reading IP address (localAddress=%s)", localAddress);
                throw new RuntimeException(message, error);
            }
        }
    }

    private HttpClientConnectionManager createConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getThreadCount());
        connectionManager.setDefaultMaxPerRoute(config.getThreadCount());
        connectionManager.setDefaultSocketConfig(createSocketConfig());
        return connectionManager;
    }

    private static SocketConfig createSocketConfig() {
        return SocketConfig
                .custom()
                .setSoKeepAlive(true)
                .setTcpNoDelay(true)
                .setSoReuseAddress(true)
                .build();
    }

}
