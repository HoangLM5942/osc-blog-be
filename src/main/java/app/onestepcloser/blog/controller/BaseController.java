package app.onestepcloser.blog.controller;

import app.onestepcloser.blog.domain.model.response.BaseResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseController {

    private static final Logger logger = LogManager.getLogger();

    protected <T, R> ResponseEntity<BaseResponse<R>> customReturn(Function<T, R> method, T request, BindingResult bindingResult) {
        logger.info("[REQUEST BODY] {}", request);
        BaseResponse<R> response = new BaseResponse<>();
        if (hasErrors(response, bindingResult)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.setData(method.apply(request));
        response.setResponseStatus(HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    protected <T, R> ResponseEntity<BaseResponse<R>> customReturn(Function<T, R> method, T request) {
        return customReturn(method, request, null);
    }

    protected <T> ResponseEntity<BaseResponse<?>> customReturn(Consumer<T> method, T request, BindingResult bindingResult) {
        logger.info("[REQUEST BODY] {}", request);
        BaseResponse<?> response = new BaseResponse<>();
        if (hasErrors(response, bindingResult)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        method.accept(request);
        response.setResponseStatus(HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    protected <T> ResponseEntity<BaseResponse<?>> customReturn(Consumer<T> method, T request) {
        return customReturn(method, request, null);
    }

    protected <T> ResponseEntity<BaseResponse<T>> customReturn(Supplier<T> method, BindingResult bindingResult) {
        BaseResponse<T> response = new BaseResponse<>();
        if (hasErrors(response, bindingResult)) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.setData(method.get());
        response.setResponseStatus(HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    protected <T> ResponseEntity<BaseResponse<T>> customReturn(Supplier<T> method)  {
        return customReturn(method,null);
    }

    protected ResponseEntity<BaseResponse<?>> customReturn() {
        BaseResponse<?> response = new BaseResponse<>(HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean hasErrors(BaseResponse<?> response, BindingResult bindingResult) {
        if (bindingResult == null || !bindingResult.hasErrors()) return false;
        response.setResponseStatus(HttpStatus.BAD_REQUEST);
        response.setErrorList(new ArrayList<>());
        bindingResult.getFieldErrors().forEach(error -> {
            response.getErrorList().add(String.format("[%s] %s", error.getField(), error.getDefaultMessage()));
        });
        return true;
    }

}
