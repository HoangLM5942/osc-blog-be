package app.onestepcloser.blog.domain.model.response;

import app.onestepcloser.blog.exception.ApiException;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Data
public class BaseResponse<T> {
    private int code = HttpStatus.OK.value();
    private String message = HttpStatus.OK.getReasonPhrase();
    private List<String> errorList;
    private T data;

    public BaseResponse() {
        super();
    }

    public BaseResponse(HttpStatus status) {
        this.setResponseStatus(status);
    }

    public BaseResponse(ApiException exception) {
        this.code=  exception.getStatus().value();
        this.message = exception.getStatus().getReasonPhrase();
        this.errorList = Collections.singletonList(exception.getMessage());
    }

    public void setResponseStatus(HttpStatus status){
        this.code=  status.value();
        this.message = status.getReasonPhrase();
    }
}
