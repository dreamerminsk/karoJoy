package ch.caro62.utils;

/**
 * @author karo62
 */
public class DateTimeUtils {

    public static String toISO(String datetime) {
        return datetime.substring(0, 4) +
                "-" +
                datetime.substring(4, 6) +
                "-" +
                datetime.substring(6, 11) +
                ":" +
                datetime.substring(11, 13) +
                ":" +
                datetime.substring(13, 15);
    }

}
