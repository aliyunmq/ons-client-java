package com.aliyun.openservices.ons.api.bean;

public class Subscription {
    private String topic;
    private String expression;

    /**
     * The type of the message tag or SQL92.
     * <br>If it is null, it is equal to TAG.
     *
     * @see com.aliyun.openservices.ons.api.ExpressionType#TAG
     * @see com.aliyun.openservices.ons.api.ExpressionType#SQL92
     * <p>消息标签或SQL92的类型。
     * <br>如果为null，则相当于 TAG。
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
