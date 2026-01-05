package zxylearn.smart_cems_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("zxylearn.smart_cems_server.mapper")
public class SmartCemsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartCemsServerApplication.class, args);
	}

}
