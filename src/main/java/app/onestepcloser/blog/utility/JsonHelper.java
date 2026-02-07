package app.onestepcloser.blog.utility;

import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonHelper {

    private JsonHelper() {}

    private static final ObjectMapper mapper = new ObjectMapper();

//    static {
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//    }

    public static String toString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static <T> T getObject(String data, Class<T> clazz) {
        try {
            return mapper.readValue(data, clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getObject(Map<String, Object> data, Class<T> clazz) {
        try {
            return mapper.convertValue(data, clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getObject(byte[] data, Class<T> clazz) {
        try {
            return mapper.readValue(data, clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getObject(InputStream inputStream, Class<T> clazz) {
        try {
            return mapper.readValue(inputStream, clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T getObjectFromMap(Map<String, String> data, Class<T> clazz) {
        try {
            return mapper.convertValue(data, clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(String jsonData) {
        try {
            return mapper.readValue(jsonData, Map.class);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getStringMap(InputStream inputStream) {
        try {
            return mapper.readValue(inputStream, Map.class);
        }
        catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static <T> String toJson(T t) {
        try {
            return mapper.writeValueAsString(t);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<?> getList(String data) {
        if (data != null) {
            try {
                return Arrays.asList(mapper.readValue(data, Long[].class));
            }
            catch (Exception e) {
                try {
                    return Arrays.asList(mapper.readValue(data, String[].class));
                }
                catch (Exception ex) {
                    return null;
                }
            }
        }
        return null;
    }

}
