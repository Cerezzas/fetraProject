package az.coders.fera_project.exception;

import az.coders.fera_project.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    // Конструктор с только errorCode
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    // Конструктор с сообщением
    public NotFoundException(String message) {
        super(message);
        this.errorCode = null; // можно сделать его null, если не хочу использовать errorCode
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
