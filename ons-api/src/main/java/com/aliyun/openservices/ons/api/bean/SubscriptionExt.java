package com.aliyun.openservices.ons.api.bean;

/**
 * The subscription extended class.
 * <p>订阅关系扩展类。
 */
public class SubscriptionExt extends Subscription {
    private boolean persistence = true;

    public boolean isPersistence() {
        return persistence;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Subscription [topic=" + super.getTopic() + ", expression=" + super.getExpression()
            + ", persistence=" + persistence + "]";
    }
}
