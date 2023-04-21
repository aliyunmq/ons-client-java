package com.aliyun.openservices.ons.client;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.SessionCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAbstract implements Admin {
    private static final Logger log = LoggerFactory.getLogger(ClientAbstract.class);
    protected SessionCredentialsProvider provider;

    public ClientAbstract(Properties properties) {
        final String ramRole = properties.getProperty(PropertyKeyConst.RAM_ROLE_NAME);
        if (StringUtils.isNotBlank(ramRole)) {
            log.info("Ram role was set"); {

            }
        }
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

    }

    @Override
    public void shutdown() {

    }
}
