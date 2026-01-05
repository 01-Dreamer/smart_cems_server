package zxylearn.smart_cems_server.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
