package courses.concordia.exception;

import courses.concordia.dto.response.Response;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Global exception handler for the application, capturing exceptions thrown by any controller
 * and returning an appropriate ResponseEntity object to the client.
 */
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    /**
     * Handles not found exceptions thrown when an entity cannot be located.
     *
     * @param ex      The caught EntityNotFoundException.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity object containing a not found status and the error message.
     */
    @ExceptionHandler(CustomExceptionFactory.EntityNotFoundException.class)
    public final ResponseEntity<?> handleNotFountExceptions(Exception ex, WebRequest request) {
        Response<?> response = Response.notFound();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles exceptions thrown when a duplicate entity is encountered.
     *
     * @param ex      The caught DuplicateEntityException.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity object containing a conflict status and the error message.
     */
    @ExceptionHandler(CustomExceptionFactory.DuplicateEntityException.class)
    public final ResponseEntity<?> handleNotFountExceptions1(Exception ex, WebRequest request) {
        Response<?> response = Response.duplicateEntity();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles custom exceptions not categorized by the other handlers.
     *
     * @param ex      The caught CustomEntityException.
     * @param request The web request during which the exception was thrown.
     * @return A ResponseEntity object containing a status of not acceptable and the error message.
     */
    @ExceptionHandler(CustomExceptionFactory.CustomEntityException.class)
    public final ResponseEntity<?> handleNotFountExceptions2(Exception ex, WebRequest request) {
        Response<?> response = Response.exception();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }
}