package com.aliyun.openservices.ons.api;

/**
 * 消息选择器
 */
public class MessageSelector {

    private ExpressionType type;
    /**
     * 表达式
     */
    private String subExpression;

    private MessageSelector() {
    }

    private MessageSelector(ExpressionType type, String subExpression) {
        this.type = type;
        this.subExpression = subExpression;
    }

    public static MessageSelector bySql(String subExpression) {
        return new MessageSelector(ExpressionType.SQL92, subExpression);
    }

    public static MessageSelector byTag(String subExpression) {
        return new MessageSelector(ExpressionType.TAG, subExpression);
    }


    public ExpressionType getType() {
        return type;
    }

    public String getSubExpression() {
        return subExpression;
    }
}