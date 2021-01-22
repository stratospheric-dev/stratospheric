package dev.stratospheric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;

@SpringBootApplication(exclude = ContextInstanceDataAutoConfiguration.class)
public class TodoApplication {

	public static void main(String[] args) {
    System.out.println("Manually triggering app update.");
	  SpringApplication.run(TodoApplication.class, args);
	}

}
