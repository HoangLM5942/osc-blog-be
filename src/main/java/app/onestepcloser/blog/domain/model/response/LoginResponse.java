package app.onestepcloser.blog.domain.model.response;

import app.onestepcloser.blog.security.UserDetailsCustom;
import app.onestepcloser.blog.utility.Constants;
import lombok.Data;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

@Data
public class LoginResponse {

    private Long id;
    private String status;
    private String username;
    private String firstName;
    private String lastName;
    private String image;
    private String token;
    private long expiresIn;
    private String type = Constants.TOKEN_PREFIX;

    public LoginResponse(UserDetailsCustom userDetails ) {
        this.id = userDetails.getId() == null ? 0 : userDetails.getId();
        this.status = StringUtils.isBlank(userDetails.getStatus()) ? Constants.EMPTY_STRING : userDetails.getStatus();
        this.username = StringUtils.isBlank(userDetails.getUsername()) ? Constants.EMPTY_STRING : userDetails.getUsername();
        this.firstName = StringUtils.isBlank(userDetails.getFirstName()) ? Constants.EMPTY_STRING : userDetails.getFirstName();
        this.lastName = StringUtils.isBlank(userDetails.getLastName()) ? Constants.EMPTY_STRING : userDetails.getLastName();
        this.image = StringUtils.isBlank(userDetails.getImage()) ? Constants.EMPTY_STRING : userDetails.getImage();
    }

    public String toJson() {
        JSONObject userInfo = new JSONObject();
        userInfo.put("id", this.id);
        userInfo.put("status", this.status);
        userInfo.put("username", this.username);
        userInfo.put("firstName", this.firstName);
        userInfo.put("lastName", this.lastName);
        userInfo.put("image", this.image);
        return userInfo.toJSONString();
    }
}
