package ru.practicum.shareit.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException500.class)
    public Map<String, String> handleException500(Exception ex, HttpServletResponse response) {
        response.setStatus(500);
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(ValidationException400.class)
    public Map<String, String> handleException400(Exception ex, HttpServletResponse response) {
        response.setStatus(400);
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(ValidationException404.class)
    public Map<String, String> handleException404(Exception ex, HttpServletResponse response) {
        response.setStatus(404);
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(ValidationException409.class)
    public Map<String, String> handleException409(Exception ex, HttpServletResponse response) {
        response.setStatus(409);
        return Map.of("error", ex.getMessage());
    }
}