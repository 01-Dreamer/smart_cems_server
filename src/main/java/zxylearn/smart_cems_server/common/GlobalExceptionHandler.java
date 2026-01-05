package zxylearn.smart_cems_server.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 仅记录错误信息，不打印堆栈，减少控制台噪音
        log.error("系统异常: {}", e.toString());
        return Result.error(e.getMessage());
    }
}

