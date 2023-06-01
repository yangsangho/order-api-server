package io.yangbob.order.app.common.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoResourceException extends RuntimeException {
    public NoResourceException(String resourceName) {
        super("[" + resourceName + "] 리소스를 찾을 수 없습니다.");
    }
}
