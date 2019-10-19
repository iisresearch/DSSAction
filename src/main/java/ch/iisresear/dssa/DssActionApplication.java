package ch.iisresear.dssa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class DssActionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DssActionApplication.class, args);
	}

}
