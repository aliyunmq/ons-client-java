package com.aliyun.openservices.ons.client;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.apis.SessionCredentials;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.apache.rocketmq.client.java.misc.ThreadFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StsSessionCredentialProvider implements SessionCredentialsProvider {
    private static final Logger log = LoggerFactory.getLogger(StsSessionCredentialProvider.class);

    private static final int HTTP_TIMEOUT_MILLIS = 1000;
    private static final String RAM_ROLE_HOST = "100.100.100.200";
    private static final String RAM_ROLE_URL_PREFIX = "/latest/meta-data/Ram/security-credentials/";
    private final HttpTinyClient httpTinyClient;
    private volatile SessionCredentials credentials;
    private final String ramRole;
    private final ExecutorService stsRefresher;

    public StsSessionCredentialProvider(String ramRole) {
        this(ramRole, HttpTinyClient.getInstance());
    }

    /**
     * Constructor for injecting of mock {@link HttpTinyClient}.
     *
     * @param ramRole    ram role name.
     * @param tinyClient http tiny client.
     */
    StsSessionCredentialProvider(String ramRole, HttpTinyClient tinyClient) {
        this.httpTinyClient = tinyClient;
        this.ramRole = ramRole;
        this.credentials = null;
        this.stsRefresher = new ThreadPoolExecutor(
            1,
            1,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryImpl("StsRefresher"));
    }

    @Override
    public SessionCredentials getSessionCredentials() {
        if (null == credentials) {
            final ListenableFuture<SessionCredentials> future = refresh();
            try {
                return future.get();
            } catch (Throwable t) {
                // TODO
                throw new RuntimeException("");
            }
        }
        if (credentials.expiredSoon()) {
            refresh();
        }
        return credentials;
    }

    static class StsCredentials {
        public static String SUCCESS_CODE = "Success";

        @SerializedName("AccessKeyId")
        private final String accessKeyId;
        @SerializedName("AccessKeySecret")
        private final String accessKeySecret;
        @SerializedName("Expiration")
        private final String expiration;
        @SerializedName("SecurityToken")
        private final String securityToken;
        @SerializedName("LastUpdated")
        private final String lastUpdated;
        @SerializedName("Code")
        private final String code;

        public StsCredentials(String accessKeyId, String accessKeySecret, String expiration, String securityToken,
            String lastUpdated, String code) {
            this.accessKeyId = accessKeyId;
            this.accessKeySecret = accessKeySecret;
            this.expiration = expiration;
            this.securityToken = securityToken;
            this.lastUpdated = lastUpdated;
            this.code = code;
        }

        public String getAccessKeyId() {
            return this.accessKeyId;
        }

        public String getAccessKeySecret() {
            return this.accessKeySecret;
        }

        public String getExpiration() {
            return this.expiration;
        }

        public String getSecurityToken() {
            return this.securityToken;
        }

        public String getLastUpdated() {
            return this.lastUpdated;
        }

        public String getCode() {
            return this.code;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private ListenableFuture<SessionCredentials> refresh() {
        String url = UtilAll.HTTP_PREFIX + RAM_ROLE_HOST + RAM_ROLE_URL_PREFIX + ramRole;
        final ListenableFuture<HttpTinyClient.HttpResult> future = httpTinyClient.httpGet(url, HTTP_TIMEOUT_MILLIS,
            stsRefresher);
        return Futures.transform(future, httpResult -> {
            try {
                if (httpResult.isOk()) {
                    final String content = httpResult.getContent();
                    Gson gson = new GsonBuilder().create();
                    final StsCredentials stsCredentials = gson.fromJson(content, StsCredentials.class);
                    final String expiration = stsCredentials.getExpiration();
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    long expiredTimeMillis = dateFormat.parse(expiration).getTime();
                    final String code = stsCredentials.getCode();
                    if (StsCredentials.SUCCESS_CODE.equals(code)) {
                        credentials = new SessionCredentials(stsCredentials.getAccessKeyId(),
                            stsCredentials.getAccessKeySecret(),
                            stsCredentials.getSecurityToken(), expiredTimeMillis);
                        return credentials;

                    }
                    log.error("Failed to fetch sts token, ramRole={}, code={}", ramRole, code);
                } else {
                    log.error("Failed to fetch sts token, ramRole={}, httpCode={}", ramRole, httpResult.getCode());
                }
            } catch (Throwable t) {
                log.error("Exception raise while refreshing sts token, ramRole={}", ramRole, t);
                throw new RuntimeException(t);
            }
            throw new RuntimeException("Failed to fetch sts token");
        }, MoreExecutors.directExecutor());
    }
}
