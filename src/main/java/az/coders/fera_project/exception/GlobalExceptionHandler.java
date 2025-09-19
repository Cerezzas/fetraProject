package az.coders.fera_project.exception;

import az.coders.fera_project.dto.ApiError;
import az.coders.fera_project.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiError apiError = new ApiError("VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(apiError);
    }

    // Обработка кастомного исключения NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
        // Используем errorCode из исключения (если он есть)
        ApiError apiError = new ApiError(
                ex.getErrorCode() != null ? ex.getErrorCode().toString() : "UNKNOWN_ERROR", // Если errorCode не передан, используем "UNKNOWN_ERROR"
                ex.getMessage()  // Сообщение, которое передается в исключении
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // Обработка кастомного исключения BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException ex) {
        // Используем общий errorCode для ошибок BadRequest
        ApiError apiError = new ApiError(
                ErrorCode.INTERNAL_SERVICE_ERROR.toString(), // Общий код ошибки
                ex.getMessage()  // Сообщение из исключения
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // Обработка других непредвиденных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex); // Используй логгер
        ApiError apiError = new ApiError(
                ErrorCode.UNKNOWN_ERROR.toString(),
                "An unexpected error occurred."
        );
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
