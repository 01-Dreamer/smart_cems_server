package zxylearn.smart_cems_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zxylearn.smart_cems_server.common.JwtUtils;
import zxylearn.smart_cems_server.common.Result;
import zxylearn.smart_cems_server.dto.LoginRequest;

import zxylearn.smart_cems_server.dto.LoginResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import zxylearn.smart_cems_server.dto.RegisterRequest;
import zxylearn.smart_cems_server.entity.SysUser;
import zxylearn.smart_cems_server.mapper.SysUserMapper;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证管理", description = "用户认证接口")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userDetails.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(userDetails.getUsername());
        // 假设角色是第一个权限
        response.setRole(userDetails.getAuthorities().stream().findFirst().get().getAuthority());

        return Result.success(response);

    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<String> register(@RequestBody RegisterRequest request) {
        if (sysUserMapper.selectCount(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        sysUserMapper.insert(user);

        return Result.success("注册成功");
    }

}
