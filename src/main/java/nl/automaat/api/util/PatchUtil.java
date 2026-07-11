package nl.automaat.api.util;

import java.util.function.Consumer;

public final class PatchUtil {

    private PatchUtil() {
    }

    public static <T> void applyIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
