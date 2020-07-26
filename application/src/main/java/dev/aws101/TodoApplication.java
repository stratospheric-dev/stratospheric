package dev.aws101;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

  @Override
  public void run(String... args) throws Exception {
    System.out.println(System.getenv("AWS_CONTAINER_CREDENTIALS_RELATIVE_URI"));
  }
}
