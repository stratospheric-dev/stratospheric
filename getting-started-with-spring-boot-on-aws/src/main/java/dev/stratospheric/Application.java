package dev.stratospheric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

@SpringBootApplication
public class Application {

  private final S3Client s3Client;

  public Application(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @EventListener(classes = ApplicationReadyEvent.class)
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    for (Bucket availableBucket : s3Client.listBuckets().buckets()) {
      System.out.println(availableBucket.name());
    }
  }
}
