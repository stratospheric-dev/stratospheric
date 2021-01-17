package dev.stratospheric.cdk;

public class Validations {

    public static void requireNonEmpty(String string, String message) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

}
