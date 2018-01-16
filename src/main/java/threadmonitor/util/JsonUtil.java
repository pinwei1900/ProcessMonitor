/*   
 * Copyright (c) 2012 by XUANWU INFORMATION TECHNOLOGY CO. 
 *             All rights reserved                         
 */
package threadmonitor.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;


/**
 * @Description: Json 操作工具类
 * @Author <a href="hw86xll@163.com">Wei.Huang</a>
 * @Date 2012-9-19
 * @Version 1.0.0
 */
public class JsonUtil {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    public static <T> T deserializeFromBytes(byte[] srcBytes, Class<T> t)
            throws Exception {
        if (srcBytes == null) {
            throw new IllegalArgumentException("srcBytes should not be null");
        }
        return mapper.readValue(srcBytes, 0, srcBytes.length, t);
    }

    public static <T> T deserialize(String src, Class<T> t) throws Exception {
        if (src == null) {
            throw new IllegalArgumentException("src should not be null");
        }
        return mapper.readValue(src, t);
    }


    public static <T> T deserializeSilently(String src, Class<T> t) {
        try {
            return mapper.readValue(src, t);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static byte[] serializeToBytes(Object obj) throws Exception {
        return mapper.writeValueAsBytes(obj);
    }

    public static String serialize(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    public static String serializeSilently(Object obj) {
        try {
            return serialize(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public static <T> String serializeType(T obj) throws Exception {
        String jsonStrObj= mapper.writeValueAsString(obj);
        return jsonStrObj;
    }

    public static <T> String serializeTypeSilently(T obj) {
        try {
            return serialize(obj);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Map<String, String> toMap(String src) throws Exception {
        if (StringUtils.isBlank(src)) {
            return Collections.emptyMap();
        }
        return mapper.readValue(src, new TypeReference<Map<String, String>>() {
        });
    }

    public static Map<String, Object> toMapSilently(String src) {
        try {
            if (StringUtils.isBlank(src)) {
                return Collections.emptyMap();
            }
            return mapper.readValue(src, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Map<String, Object> toObjectMap(String src) throws Exception {
        if (StringUtils.isBlank(src)) {
            return Collections.emptyMap();
        }
        return mapper.readValue(src, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * @param data json data
     * @param msg  message
     * @param ret  0--true, not 0--false
     * @return
     */
    public static String toJSON(String data, String msg, int ret) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"data\":");
        sb.append(data);
        sb.append(",\"msg\":\"");
        sb.append(msg);
        sb.append("\",\"ret\":");
        sb.append(ret);
        sb.append('}');
        return sb.toString();
    }

    public static String toJSON(String msg, int ret) {
        return toJSON("null", msg, ret);
    }

    public static String toJSON(int ret) {
        String msg = "Error data!";
        if (ret == 0) {
            msg = "ok";
        }
        return toJSON("null", msg, ret);
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> t) {
        Assert.notNull(json, "Json string should not be null");
        try {
            return mapper.readValue(json, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> List<T> jsonToList(String jsonStr, Class<T> t) {
        Assert.notNull(jsonStr, "Json string should not be null");
        try {
            return mapper.readValue(jsonStr, mapper.getTypeFactory().constructCollectionType(List.class, t));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
