package app.onestepcloser.blog.exception;

import app.onestepcloser.blog.domain.model.response.BaseResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

	private final Logger logger = LogManager.getLogger();

	@org.springframework.web.bind.annotation.ExceptionHandler(Throwable.class)
	public ResponseEntity<BaseResponse<?>> handleConversion(Exception e) {
		logger.error("----- [ERROR]: ", e);
		if (e instanceof ApiException apiException) {
            BaseResponse<Object> response = new BaseResponse<>(apiException);
			return new ResponseEntity<>(response, apiException.getStatus());
		} else {
			BaseResponse<Object> response = new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}