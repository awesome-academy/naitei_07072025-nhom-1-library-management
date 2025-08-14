package org.librarymanagement.exception;

import org.librarymanagement.dto.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Xử lý các trường hợp không tìm thấy tài nguyên
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseObject> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.badRequest()
                .body(new ResponseObject(
                        "Có lỗi : không tìm thấy tài nguyen",
                        HttpStatus.NOT_FOUND.value(),
                        null
                ));
    }
    // Fallback
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(new ResponseObject(
                        "Có lỗi xảy ra rồi ní ơi",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        null
                ));
    }
}
