package zxylearn.smart_cems_server.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zxylearn.smart_cems_server.common.EmailUtil;
import zxylearn.smart_cems_server.common.JwtUtils;
import zxylearn.smart_cems_server.dto.CaptchaResponse;
import zxylearn.smart_cems_server.dto.LoginRequest;
import zxylearn.smart_cems_server.dto.LoginResponse;
import zxylearn.smart_cems_server.dto.RegisterRequest;
import zxylearn.smart_cems_server.entity.SysUser;
import zxylearn.smart_cems_server.mapper.SysUserMapper;
import zxylearn.smart_cems_server.service.AuthService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    private static final String CAPTCHA_IMAGE_KEY = "auth:captcha:image:";
    private static final String CAPTCHA_EMAIL_KEY = "auth:captcha:email:";
    private static final String JWT_BLACKLIST_KEY = "auth:jwt:blacklist:";

    @Override
    public CaptchaResponse getImageCaptcha() {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String uuid = IdUtil.simpleUUID();
        String code = lineCaptcha.getCode();
        
        redisTemplate.opsForValue().set(CAPTCHA_IMAGE_KEY + uuid, code, 3, TimeUnit.MINUTES);

        CaptchaResponse response = new CaptchaResponse();
        response.setUuid(uuid);
        response.setBase64Img(lineCaptcha.getImageBase64Data());
        return response;
    }

    @Override
    public void sendEmailCaptcha(String email) {
        // 检查邮箱是否已注册
        if (sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, email)) > 0) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 检查发送频率 (60s)
        String key = CAPTCHA_EMAIL_KEY + email;
        if (redisTemplate.hasKey(key)) {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (expire != null && expire > 240) { // 5分钟有效期，如果剩余时间大于4分钟(即过去不到1分钟)，则限制
                throw new RuntimeException("验证码发送太频繁，请稍后再试");
            }
        }

        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);

        emailUtil.sendHtmlMail(email, "智能校园能耗监控系统 - 注册验证码", 
                "<h3>您的注册验证码是：<span style='color:red'>" + code + "</span></h3><p>有效期5分钟，请勿泄露。</p>");
    }

    @Override
    public void register(RegisterRequest request) {
        // 验证邮箱验证码
        String key = CAPTCHA_EMAIL_KEY + request.getUsername();
        String cachedCode = (String) redisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equals(request.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }

        // 再次检查是否注册
        if (sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername())) > 0) {
            throw new RuntimeException("该邮箱已被注册");
        }

        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        sysUserMapper.insert(user);

        // 注册成功后删除验证码
        redisTemplate.delete(key);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 验证图形验证码
        String key = CAPTCHA_IMAGE_KEY + request.getUuid();
        String cachedCode = (String) redisTemplate.opsForValue().get(key);
        if (cachedCode == null || !cachedCode.equalsIgnoreCase(request.getCode())) {
            throw new RuntimeException("验证码错误或已过期");
        }
        redisTemplate.delete(key); // 验证成功后立即删除

        // 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();
        String token = jwtUtils.generateToken(userDetails.getUsername(), role);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(userDetails.getUsername());
        response.setRole(role);

        return response;
    }

    @Override
    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // 获取剩余过期时间
        Date expiration = jwtUtils.extractExpiration(token);
        long ttl = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        
        if (ttl > 0) {
            String jti = jwtUtils.extractJti(token);
            redisTemplate.opsForValue().set(JWT_BLACKLIST_KEY + jti, "blacklisted", ttl, TimeUnit.SECONDS);
        }
    }
}
