package com.example.test2.dao;

import org.springframework.dao.DuplicateKeyException;

public class DuplicateUserIdException extends RuntimeException{
    public DuplicateUserIdException(Throwable cause){
        super(cause);
    }
}
