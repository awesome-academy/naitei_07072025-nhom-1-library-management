package org.librarymanagement.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class NotEnoughConditionsBorrowException extends RuntimeException{
    private  final Map<String,String> fieldErrors;
    public NotEnoughConditionsBorrowException(Map<String,String> fieldErrors){
        super("Không thể mượn sách");
        this.fieldErrors = fieldErrors;
    }
}
