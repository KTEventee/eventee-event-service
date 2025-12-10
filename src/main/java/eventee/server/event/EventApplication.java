package eventee.server.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

@SpringBootApplication
@EnableFeignClients(basePackages = "eventee.server.event.domain.infrastructure.client")
public class EventApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventApplication.class, args);
	}

}
