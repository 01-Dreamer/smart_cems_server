package zxylearn.smart_cems_server.service;

import zxylearn.smart_cems_server.dto.CaptchaResponse;
import zxylearn.smart_cems_server.dto.LoginRequest;
import zxylearn.smart_cems_server.dto.LoginResponse;
import zxylearn.smart_cems_server.dto.RegisterRequest;

public interface AuthService {
    CaptchaResponse getImageCaptcha();
    void sendEmailCaptcha(String email);
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
}
