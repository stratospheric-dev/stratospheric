package dev.stratospheric;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketLocationRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;

@Controller
public class DashboardController {

  private final String bucketName;
  private final S3Client s3Client;

  private String bucketLocation;

  public DashboardController(
    @Value("${custom.bucket-name}") String bucketName,
    S3Client s3Client) {
    this.bucketName = bucketName;
    this.s3Client = s3Client;
  }

  @PostConstruct
  public void postConstruct() {
    this.bucketLocation = String.format("https://%s.s3.%s.amazonaws.com",
      bucketName, this.s3Client.getBucketLocation(GetBucketLocationRequest.builder()
        .bucket(bucketName).build()));
  }

  @GetMapping("/")
  public ModelAndView getDashboardView() {
    ModelAndView modelAndView = new ModelAndView("dashboard");
    modelAndView.addObject("message", "Spring Boot with AWS");
    modelAndView.addObject("bucketName", bucketName);
    modelAndView.addObject("bucketLocation", bucketLocation);
    modelAndView.addObject("availableFiles", s3Client.listObjects(ListObjectsRequest.builder().bucket(bucketName).build()).contents());
    return modelAndView;
  }
}
