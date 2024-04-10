package courses.concordia.exception;

/**
 * Enum defining the types of exceptions that can be thrown within the application.
 * Each exception type is associated with a specific error message key for internationalization purposes.
 */
public enum ExceptionType {
    ENTITY_NOT_FOUND("not.found"),
    DUPLICATE_ENTITY("duplicate"),
    CUSTOM_EXCEPTION("custom");

    private final String value;

    ExceptionType(String value) {
        this.value = value;
    }

    String getValue() {
        return this.value;
    }
}