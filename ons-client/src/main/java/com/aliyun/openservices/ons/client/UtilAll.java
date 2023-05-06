package com.aliyun.openservices.ons.client;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class UtilAll {
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";

    private static final Pattern NAME_SERVER_ENDPOINT_PATTERN = Pattern.compile("^(\\w+://|).*");

    private UtilAll() {
    }

    public static boolean validateNameServerEndpoint(String endpoint) {
        return StringUtils.isNoneBlank(endpoint) && NAME_SERVER_ENDPOINT_PATTERN.matcher(endpoint).matches();
    }
}
