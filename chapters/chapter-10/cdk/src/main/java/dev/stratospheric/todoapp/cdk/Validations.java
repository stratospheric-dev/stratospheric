package dev.stratospheric.todoapp.cdk;

public class Validations {

    private Validations() {}

    public static void requireNonEmpty(String string, String message) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
