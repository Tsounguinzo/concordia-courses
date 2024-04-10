package courses.concordia.exception;

import courses.concordia.config.PropertiesConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Optional;


/**
 * Factory class for creating and throwing custom exceptions based on entity types and exception scenarios.
 * Utilizes {@link PropertiesConfig} to fetch custom error messages based on configurations.
 */
@Component
public class CustomExceptionFactory {

    private static PropertiesConfig propertiesConfig;

    /**
     * Autowires the PropertiesConfig to enable fetching of custom error messages.
     *
     * @param propertiesConfig Configuration properties for error messages.
     */
    @Autowired
    public CustomExceptionFactory(PropertiesConfig propertiesConfig) {
        CustomExceptionFactory.propertiesConfig = propertiesConfig;
    }

    /**
     * Constructs and throws a custom runtime exception based on the provided entity type, exception type, and arguments.
     *
     * @param entityType     The type of the entity related to the exception.
     * @param exceptionType  The category of the exception being thrown.
     * @param args           Additional arguments to format the exception message.
     * @return               A custom RuntimeException tailored to the specific error scenario.
     */
    public static RuntimeException throwCustomException(EntityType entityType, ExceptionType exceptionType, String... args) {
        String messageTemplate = getMessageTemplate(entityType, exceptionType);
        return constructException(exceptionType, messageTemplate, args);
    }

    /**
     * Helper method to construct a specific RuntimeException based on the exception type.
     *
     * @param exceptionType  The type of the exception.
     * @param messageTemplate The message template for the exception.
     * @param args           Arguments used to format the exception message.
     * @return               A specific RuntimeException instance.
     */
    private static RuntimeException constructException(ExceptionType exceptionType, String messageTemplate, String... args) {
        String formattedMessage = formatMessage(messageTemplate, args);

        return switch (exceptionType) {
            case ENTITY_NOT_FOUND -> new EntityNotFoundException(formattedMessage);
            case DUPLICATE_ENTITY -> new DuplicateEntityException(formattedMessage);
            case CUSTOM_EXCEPTION -> new CustomEntityException(formattedMessage);
            default -> throw new IllegalArgumentException("Unsupported exception type: " + exceptionType);
        };
    }

    /**
     * Generates a message template identifier based on the entity type and exception type.
     *
     * @param entityType    The type of the entity.
     * @param exceptionType The type of the exception.
     * @return              A string identifier used to fetch the custom error message template.
     */
    private static String getMessageTemplate(EntityType entityType, ExceptionType exceptionType) {
        return entityType.name().toLowerCase() + "." + exceptionType.getValue();
    }

    /**
     * Formats the error message using a template fetched from {@link PropertiesConfig}.
     *
     * @param template The message template identifier.
     * @param args     Arguments for message formatting.
     * @return         The formatted error message.
     */
    private static String formatMessage(String template, String... args) {
        Optional<String> templateContent = Optional.ofNullable(propertiesConfig.getConfigValue(template));
        return templateContent
                .map(content -> MessageFormat.format(content, (Object[]) args))
                .orElseGet(() -> String.format(template, (Object[]) args));
    }

    /**
     * Custom exception for scenarios where an entity is not found.
     */
    public static class EntityNotFoundException extends RuntimeException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for scenarios involving duplicate entities.
     */
    public static class DuplicateEntityException extends RuntimeException {
        public DuplicateEntityException(String message) {
            super(message);
        }
    }

    /**
     * Custom exception for other custom-defined scenarios.
     */
    public static class CustomEntityException extends RuntimeException {
        public CustomEntityException(String message) {
            super(message);
        }
    }

}
