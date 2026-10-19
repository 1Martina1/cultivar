package com.martina.cultivar;

public class PhUtils {
    public static boolean nelRange(double ph, double min, double max) {
        return ph >= min && ph <= max;
    }
}