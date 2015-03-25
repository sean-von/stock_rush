package com.smikevon.stock.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 冯枭 E-mail:fengxiao@xiaomi.com
 * @since 创建时间: 14-11-12 下午8:09
 */
public class JsonUtils {

    private static final Logger LOGGER = Logger.getLogger(JsonUtils.class);

    public static String obj2json(Object obj) throws IOException {
        StringWriter out = new StringWriter();
        new ObjectMapper().getJsonFactory().createJsonGenerator(out).writeObject(obj);
        return out.toString();
    }

    public static Object json2obj(String json, Class<?> clz) throws IOException {
        return new ObjectMapper().readValue(json, clz);
    }

    public static String jsonFormatter(String uglyJSONString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJSONString);
        String prettyJsonString = gson.toJson(je);
        return prettyJsonString;
    }

    public static void main(String[] args) throws IOException {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name1", "value1");
        List<String> list = new LinkedList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        map.put("name2", list);
        map.put("name3", Boolean.valueOf(true));
        map.put("name4", "你好，世界");
        LOGGER.info(JsonUtils.obj2json(map));
        LOGGER.info(JsonUtils.jsonFormatter(JsonUtils.obj2json(map)));
    }
}
