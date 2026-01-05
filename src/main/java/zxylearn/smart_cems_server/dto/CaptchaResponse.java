package zxylearn.smart_cems_server.dto;

import lombok.Data;

@Data
public class CaptchaResponse {
    private String uuid;
    private String base64Img;
}
