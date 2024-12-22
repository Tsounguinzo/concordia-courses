package courses.concordia.controller.request;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ConcordiaEmailValidator.class)
@Documented
public @interface ConcordiaEmail {
    String message() default "Invalid email domain";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

