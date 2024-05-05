package com.intention.web.utils;

import java.util.stream.Collectors;

public class ResourceNameUtils {

    public static String convertCamelCaseToDashSeparated(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        } else {
            String result = input.chars()
                    .mapToObj(c -> Character.isUpperCase(c)
                            ? "-" + Character.toLowerCase((char) c)
                            : String.valueOf((char) c))
                    .collect(Collectors.joining());
            return result.startsWith("-") ? result.substring(1) : result;
        }
    }

    public static String convertDashSeparatedToDotSeparated(String input) {
        return input.replace(".", "-");
    }
}