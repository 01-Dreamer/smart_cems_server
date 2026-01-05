package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zxylearn.smart_cems_server.common.Result;
import zxylearn.smart_cems_server.dto.CaptchaResponse;
import zxylearn.smart_cems_server.dto.LoginRequest;
import zxylearn.smart_cems_server.dto.LoginResponse;
import zxylearn.smart_cems_server.dto.RegisterRequest;
import zxylearn.smart_cems_server.service.AuthService;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户认证接口")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/captcha/image")
    @Operation(summary = "获取图形验证码")
    public Result<CaptchaResponse> getImageCaptcha() {
        return Result.success(authService.getImageCaptcha());
    }

    @GetMapping("/captcha/email")
    @Operation(summary = "获取邮箱验证码")
    public Result<String> getEmailCaptcha(@RequestParam String email) {
        authService.sendEmailCaptcha(email);
        return Result.success("验证码已发送");
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return Result.success(authService.login(loginRequest));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success("注册成功");
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public Result<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return Result.success("退出成功");
    }
}
