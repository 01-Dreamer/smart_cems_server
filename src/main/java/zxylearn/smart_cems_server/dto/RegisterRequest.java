package zxylearn.smart_cems_server.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username; // 邮箱
    private String password;
    private String code; // 邮箱验证码
}
