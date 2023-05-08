package com.aliyun.openservices.ons.client;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.SessionCredentials;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAbstract implements Admin {
    private static final Logger log = LoggerFactory.getLogger(ClientAbstract.class);
    protected final ClientConfiguration clientConfiguration;
    protected final AtomicBoolean started = new AtomicBoolean(false);
    private volatile SessionCredentials sessionCredentials;

    public ClientAbstract(Properties properties) {
        final String ramRole = properties.getProperty(PropertyKeyConst.RAM_ROLE_NAME);
        SessionCredentialsProvider provider;
        if (StringUtils.isNotBlank(ramRole)) {
            log.info("Ram role was set");
            provider = new StsSessionCredentialProvider(ramRole);
        } else {
            log.debug("Ram role was not set, using AccessKey/SecretKey/SecurityToken instead.");
            final String accessKey = properties.getProperty(PropertyKeyConst.AccessKey);
            final String secretKey = properties.getProperty(PropertyKeyConst.SecretKey);
            final String securityToken = properties.getProperty(PropertyKeyConst.SecurityToken);

            if (StringUtils.isBlank(accessKey)) {
                throw new ONSClientException("AccessKey is blank unexpectedly, please set it.");
            }
            if (StringUtils.isBlank(secretKey)) {
                throw new ONSClientException("SecretKey is blank unexpectedly, please set it.");
            }
            if (StringUtils.isBlank(securityToken)) {
                sessionCredentials = new SessionCredentials(accessKey, secretKey);
            } else {
                sessionCredentials = new SessionCredentials(accessKey, secretKey, securityToken);
            }
            provider = () -> sessionCredentials;
        }
        final String nameServerAddr = properties.getProperty(PropertyKeyConst.NAMESRV_ADDR);
        if (!UtilAll.validateNameServerEndpoint(nameServerAddr)) {
            throw new ONSClientException("Name server address is illegal.");
        }
        this.clientConfiguration = ClientConfiguration.newBuilder().setEndpoints(nameServerAddr)
            .setCredentialProvider(provider).build();
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public void start() {
    }

    @Override
    public void updateCredential(Properties credentialProperties) {
        final String accessKey = credentialProperties.getProperty(PropertyKeyConst.AccessKey);
        final String secretKey = credentialProperties.getProperty(PropertyKeyConst.SecretKey);
        final String securityToken = credentialProperties.getProperty(PropertyKeyConst.SecurityToken);
        if (StringUtils.isBlank(accessKey)) {
            throw new ONSClientException("update credential failed. please set access key.");
        }
        if (StringUtils.isBlank(secretKey)) {
            throw new ONSClientException("update credential failed. please set secret key");
        }
        if (StringUtils.isBlank(securityToken)) {
            this.sessionCredentials = new SessionCredentials(accessKey, secretKey);
            return;
        }
        this.sessionCredentials = new SessionCredentials(accessKey, secretKey, securityToken);
    }

    @Override
    public void shutdown() {
    }
}
