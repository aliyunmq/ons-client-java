package com.aliyun.openservices.ons.api.bean;

public class Subscription {
    private String topic;
    private String expression;

    /**
     * TAG or SQL92
     * <br>if null, equals to TAG
     *
     * @see com.aliyun.openservices.ons.api.ExpressionType#TAG
     * @see com.aliyun.openservices.ons.api.ExpressionType#SQL92
     */
    private String type;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Subscription other = (Subscription) obj;
        if (topic == null) {
            if (other.topic != null) {
                return false;
            }
        } else if (!topic.equals(other.topic)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Subscription [topic=" + topic + ", expression=" + expression + ", type=" + type + "]";
    }
}
