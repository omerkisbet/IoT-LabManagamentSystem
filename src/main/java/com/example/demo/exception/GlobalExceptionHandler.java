package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
     * Geçersiz dosya türü, boş dosya veya güvensiz dosya adı.
     */
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidFile(
            InvalidFileException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }

    /*
     * Dosyanın diske kaydedilememesi veya okunamaması.
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, Object>> handleFileStorage(
            FileStorageException exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "File storage operation failed: {}",
                request.getRequestURI(),
                exception
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                request
        );
    }

    /*
     * application.properties içerisinde belirtilen dosya
     * boyutu sınırı aşıldığında çalışır.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaximumUploadSize(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "The uploaded file cannot exceed 5 MB.",
                request
        );
    }

    /*
     * multipart/form-data isteğinin hatalı gönderilmesi.
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleMultipartException(
            MultipartException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "The multipart request is invalid.",
                request
        );
    }

    /*
     * form-data isteğinde file alanının hiç gönderilmemesi.
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<Map<String, Object>> handleMissingRequestPart(
            MissingServletRequestPartException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Required request part is missing: "
                        + exception.getRequestPartName(),
                request
        );
    }

    /*
     * @Valid ile doğrulanan request body alanlarında
     * validation hatası oluştuğunda çalışır.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(fieldError ->
                        validationErrors.put(
                                fieldError.getField(),
                                fieldError.getDefaultMessage() == null
                                        ? "Invalid value."
                                        : fieldError.getDefaultMessage()
                        )
                );

        Map<String, Object> response =
                createBaseResponse(
                        HttpStatus.BAD_REQUEST,
                        "Request validation failed.",
                        request
                );

        response.put("validationErrors", validationErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /*
     * PathVariable ve RequestParam üzerindeki method-level
     * validation hataları için.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleMethodValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "One or more request parameters are invalid.",
                request
        );
    }

    /*
     * MongoDB unique index ihlali.
     * Örneğin aynı studentNumber iki kez kaydedilirse.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKey(
            DuplicateKeyException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "A record with the same unique value already exists.",
                request
        );
    }

    /*
     * Servis katmanında elle yaptığımız kontroller.
     * Örneğin bitiş tarihinin başlangıçtan önce olması.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request
        );
    }

    /*
     * Enum, sayı veya başka bir path/query parametresinin
     * yanlış formatta gönderilmesi.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String message =
                "Invalid value for parameter '"
                        + exception.getName()
                        + "': "
                        + exception.getValue();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    /*
     * Bozuk JSON, geçersiz enum değeri veya yanlış tarih formatı.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "The JSON body is invalid, or an enum/date value has an incorrect format.",
                request
        );
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDelivery(
            EmailDeliveryException exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Email delivery failed at {}",
                request.getRequestURI(),
                exception
        );

        return buildResponse(
                HttpStatus.BAD_GATEWAY,
                exception.getMessage(),
                request
        );
    }

    /*
     * Yukarıdaki handler'ların yakalamadığı beklenmeyen hatalar.
     * İç hata ayrıntısı kullanıcıya gönderilmez; yalnızca loglanır.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "Unexpected error occurred at {}",
                request.getRequestURI(),
                exception
        );

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected server error occurred.",
                request
        );
    }

    private ResponseEntity<Map<String, Object>> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        Map<String, Object> response =
                createBaseResponse(status, message, request);

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private Map<String, Object> createBaseResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put(
                "message",
                message == null || message.isBlank()
                        ? "An error occurred."
                        : message
        );
        response.put("path", request.getRequestURI());

        return response;
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request
        );
    }
}