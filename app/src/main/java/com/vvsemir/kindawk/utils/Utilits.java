package com.vvsemir.kindawk.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilits {
    public static String extractPattern(String str, String extStr){
        Pattern pattern = Pattern.compile(extStr);
        Matcher match = pattern.matcher(str);

        if (match.find()) {
            return match.toMatchResult().group(1);
        }

        return null;
     }

    public static <T> T[] appendToArray(T[] arr, T element) {
        final int length = arr.length;
        arr = Arrays.copyOf(arr, length + 1);
        arr[length] = element;

        return arr;
    }
}
