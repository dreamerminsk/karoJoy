package cc.joyreactor.utils;

import com.sun.istack.internal.NotNull;

public class Strings {

    public static String getLastSplitComponent(@NotNull String text, @NotNull String regex) {
        String[] parts = text.split(regex);
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return null;
    }


}
