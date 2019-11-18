package cc.joyreactor.utils;

public class Integers {

    public static int of(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
