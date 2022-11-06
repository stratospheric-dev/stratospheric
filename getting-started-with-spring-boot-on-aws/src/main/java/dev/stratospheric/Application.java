package dev.stratospheric;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Application {

  private final AmazonS3 amazonS3;

  public Application(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    for (Bucket availableBuckets : amazonS3.listBuckets()) {
      System.out.println(availableBuckets.getName());
    }
  }
}
