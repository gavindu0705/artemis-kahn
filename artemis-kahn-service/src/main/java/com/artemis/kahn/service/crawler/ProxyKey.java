package com.artemis.kahn.service.crawler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by dxy on 2015/10/8.
 */
public class ProxyKey {
    private String domain;
    private int port;

    public ProxyKey(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return domain + ":" + port;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.domain).append(this.port).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ProxyKey)) {
            return false;
        }
        ProxyKey other = (ProxyKey) obj;
        return new EqualsBuilder().append(this.domain, other.domain).append(this.port, other.port).isEquals();
    }
}
