package zxylearn.smart_cems_server.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private String uuid; // 图形验证码UUID
    private String code; // 图形验证码
}
