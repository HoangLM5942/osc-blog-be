package app.onestepcloser.blog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

	@Getter
	private final int code;
	@Getter
	private final HttpStatus status;
	private final String message;

	public ApiException(HttpStatus status) {
		super(status.getReasonPhrase());
		this.code = status.value();
		this.message = status.getReasonPhrase();
		this.status = status;
	}

	public ApiException(HttpStatus status, String errorMsg) {
		super(errorMsg);
		this.code = status.value();
		this.message = errorMsg;
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
