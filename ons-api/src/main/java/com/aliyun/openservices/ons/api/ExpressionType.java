package com.aliyun.openservices.ons.api;

public enum ExpressionType {
    /**
     * 消息属性过滤，采用简单的SQL语法。
     * <p>
     * 语法关键字: {@code AND, OR, NOT, BETWEEN, IN, TRUE, FALSE, IS, NULL}
     * <p>
     * 数据类型:
     * <ul>
     * <li>布尔, 如: TRUE, FALSE</li>
     * <li>字符, 如: 'abc'</li>
     * <li>整数, 如: 123</li>
     * <li>长整数, 如: 123L</li>
     * <li>浮点数, 如: 3.1415</li>
     * </ul>
     * <p>
     * 语法:
     * <ul>
     * <li>{@code AND, OR}</li>
     * <li>{@code >, >=, <, <=, =}</li>
     * <li>{@code BETWEEN A AND B}, 等价于 {@code >=A AND <=B}</li>
     * <li>{@code NOT BETWEEN A AND B}, 等价于 {@code >B OR <A}</li>
     * <li>{@code IN ('a', 'b')}, 等价于 {@code ='a' OR ='b'}, 只支持String类型.</li>
     * <li>{@code IS NULL}, {@code IS NOT NULL}, 检查属性是否为NUll.</li>
     * <li>{@code =TRUE}, {@code =FALSE}, 检查属性为真或假.</li>
     * </ul>
     * <p>
     * 如:
     * <br>{@code (a > 10 AND a < 100) OR (b IS NOT NULL AND b=TRUE) }
     * <br> 表示需要属性a大于10并且a小于100，或者需要属性b不为空并且为真的消息
     */
    SQL92,
    /**
     * 消息标签过滤，只支持或运算，eg: "tag1 || tag2 || tag3"
     */
    TAG
}
