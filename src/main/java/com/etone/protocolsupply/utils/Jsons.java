/*
 * Copyright (c) 2018. Beijing QuarkIoe Technology Co.,Ltd.
 * All rights reserved.
 */
package com.etone.protocolsupply.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Jsons {
    protected static     Logger       logger = LoggerFactory.getLogger(Jsons.class);
    // members
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将Json字符串反序列化为Map接口类型
     *
     * @param jsonString Json字符串
     * @return 返回Map接口类型
     */
    public static Map toMap(String jsonString) {
        Map map = null;
        try {
            map = mapper.readValue(jsonString, LinkedHashMap.class);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return map;
    }
}
