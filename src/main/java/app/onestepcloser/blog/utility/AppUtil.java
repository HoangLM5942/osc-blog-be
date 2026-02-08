package app.onestepcloser.blog.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class AppUtil {
    
    private AppUtil() {}

    public static List<Long> convertListStringToLong(String[] array) {
        if (CollectionUtils.isEmpty(Arrays.asList(array))) return null;
        try {
            return Arrays.stream(array).map(Long::valueOf).collect(Collectors.toList());
        }
        catch (Exception e) {
            return null;
        }
    }

    public static List<Long> convertListStringToLong(List<String> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        try {
            return list.stream().map(Long::valueOf).collect(Collectors.toList());
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String removeSymbol(String s) {
        if (StringUtils.isBlank(s)) return s;
        String result = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        result = pattern.matcher(result).replaceAll(Constants.EMPTY_STRING);
        result = result.replaceAll("đ", "d").replaceAll("\\s{2,}", Constants.BLANK_STRING);
        return result.trim();
    }

    public static String removeSymbolV2(String s) {
        if (StringUtils.isBlank(s)) return s;
        String result = removeSymbol(s);
        result = Pattern.compile("[^A-Za-z0-9\\s]+").matcher(result).replaceAll(Constants.BLANK_STRING);
        result = Pattern.compile("\\s+").matcher(result).replaceAll(Constants.BLANK_STRING);
        return result.trim();
    }

    public static String removeMultiSpaces(String s) {
        if (StringUtils.isBlank(s)) return s;
        return s.replaceAll("\\s{2,}", Constants.BLANK_STRING);
    }

    public static String genSlug(String name, Long number) {
        if (StringUtils.isBlank(name)) return name;
        String slug = removeSymbolV2(name);
        slug = slug
                .replaceAll("\\s", Constants.DASH)
                .replace(Constants.COMMA, Constants.EMPTY_STRING)
                .replace(Constants.DOT, Constants.EMPTY_STRING);
        if (number != null) slug += Constants.DASH + number;
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String convertCamelToSnakeStyle(String camel) {
        if (StringUtils.isBlank(camel)) return camel;
        String snake = camel.substring(0, 1).toLowerCase() + camel.substring(1);
        Matcher matcher = Pattern.compile("[A-Z]").matcher(snake);
        while (matcher.find()) {
            snake = snake.replace(matcher.group(), "_" + matcher.group().toLowerCase());
        }
        return snake;
    }

    public static String randomString(int length) {
        Random random = new Random();
        return random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomString() {
        return randomString(10);
    }

    public static <T> List<T> convertUnknownObject(List<Object[]> objects, Class<T> clazz) {
        List<T> objectList = new ArrayList<>();
        for (Object[] objectArr : objects) {
            if (objectArr == null) continue;
            int i = 0;
            boolean isEmptyOject = false;
            Constructor<T> constructor;
            T object;
            try {
                constructor = clazz.getConstructor();
                object = constructor.newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                continue;
            }
            for (Field field : clazz.getDeclaredFields()) {
                if (i >= objectArr.length) {
                    isEmptyOject = i == 0;
                    break;
                }
                try {
                    field.setAccessible(true);
                    switch (field.getType().toString()) {
                        case "class java.lang.String": {
                            field.set(object, objectArr[i] == null ? null : objectArr[i].toString());
                            break;
                        }
                        case "class java.util.Date": {
                            field.set(object, objectArr[i]);
                            break;
                        }
                        case "class java.lang.Long":
                        case "long": {
                            field.set(object, objectArr[i] == null ? 0 : ((Number) objectArr[i]).longValue());
                            break;
                        }
                        case "class java.lang.Integer":
                        case "int": {
                            field.set(object, objectArr[i] == null ? 0 : ((Number) objectArr[i]).intValue());
                            break;
                        }
                        case "class java.lang.Byte":
                        case "byte": {
                            field.set(object, objectArr[i] == null ? 0 : ((Number) objectArr[i]).byteValue());
                            break;
                        }
                    }
                } catch (Exception e) {
                    isEmptyOject = (i == 0);
                }
                i++;
            }
            if (isEmptyOject) continue;
            objectList.add(object);
        }
        return objectList;
    }

    /*public static void main(String[] args) {

    }*/
}
