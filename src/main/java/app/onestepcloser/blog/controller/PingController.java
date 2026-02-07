package app.onestepcloser.blog.controller;

import app.onestepcloser.blog.domain.model.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ping")
public class PingController extends BaseController {

    @RequestMapping(method = RequestMethod.GET,  produces = {"application/json"})
    public ResponseEntity<BaseResponse<?>> get() {
        return customReturn();
    }

}
