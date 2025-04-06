package com.chuwa.itemservice.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class IdGenerator {
    private static final Snowflake snowflake = IdUtil.getSnowflake(1, 1); // workerId, datacenterId

    public static long generateId() {
        return snowflake.nextId();
    }
}
