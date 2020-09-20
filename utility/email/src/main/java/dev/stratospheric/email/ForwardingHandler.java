package dev.stratospheric.email;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

public class ForwardingHandler implements RequestHandler<S3Event, Void> {

  private static final String EMAIL_TOM = System.getenv("EMAIL_TOM");
  private static final String EMAIL_BJOERN = System.getenv("EMAIL_BJOERN");
  private static final String EMAIL_PHILIP = System.getenv("EMAIL_PHILIP");

  private LambdaLogger logger;

  @Override
  public Void handleRequest(S3Event s3Event, Context context) {

    this.logger = context.getLogger();

    String bucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
    String key = s3Event.getRecords().get(0).getS3().getObject().getKey();
    logger.log("New E-Mail received: " + bucket + "/" + key);

    AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    logger.log("Connection to S3 established");

    // TODO: Read uploaded email and forward it correctly

    sendEmail("Hello World!", "Hello", EMAIL_PHILIP);

    return null;
  }

  private void sendEmail(String content, String subject, String recipient) {
    try {
      AmazonSimpleEmailService client =
        AmazonSimpleEmailServiceClientBuilder.standard()
          .withRegion(Regions.EU_WEST_1).build();

      SendEmailRequest request = new SendEmailRequest()
        .withDestination(
          new Destination().withToAddresses(recipient))
        .withMessage(new Message()
          .withBody(new Body()
            .withHtml(new Content()
              .withCharset("UTF-8").withData(content))
            .withText(new Content()
              .withCharset("UTF-8").withData(content)))
          .withSubject(new Content()
            .withCharset("UTF-8").withData(subject)))
        .withSource("info@stratospheric.dev");

      client.sendEmail(request);
      logger.log("Email forwarded to " + recipient);
    } catch (Exception ex) {
      logger.log("The email was not sent. Error message: " + ex.getMessage());
    }
  }
}
