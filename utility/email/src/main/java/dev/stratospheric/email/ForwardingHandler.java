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
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    try {
      MimeMessageParser messageParser = new MimeMessageParser(new MimeMessage(null, s3Client.getObject(bucket, key).getObjectContent()));
      messageParser = messageParser.parse();

      String plainContent = messageParser.getPlainContent();
      String htmlContent = messageParser.getHtmlContent();
      String from = messageParser.getFrom();
      String subject = "Forwarded (Stratospheric) Mail: " + messageParser.getSubject();

      List<Address> receivers = messageParser.getTo();

      Set<String> forwardingRecipients = new HashSet<>();

      for (Address address : receivers) {
        final String emailAddress = address.toString();
        if (emailAddress.contains("info")) {
          forwardingRecipients.add(EMAIL_BJOERN);
          forwardingRecipients.add(EMAIL_PHILIP);
          forwardingRecipients.add(EMAIL_TOM);
        }

        if (emailAddress.contains("bjoern")) {
          forwardingRecipients.add(EMAIL_BJOERN);
        }

        if (emailAddress.contains("philip")) {
          forwardingRecipients.add(EMAIL_PHILIP);
        }

        if (emailAddress.contains("tom")) {
          forwardingRecipients.add(EMAIL_TOM);
        }
      }

      for (String forwardingRecipient : forwardingRecipients) {
        sendEmail(plainContent, htmlContent, subject, forwardingRecipient, from);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private void sendEmail(String plainContent, String htmlContent, String subject, String recipient, String from) {
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
              .withCharset("UTF-8").withData(htmlContent))
            .withText(new Content()
              .withCharset("UTF-8").withData(plainContent)))
          .withSubject(new Content()
            .withCharset("UTF-8").withData(subject)))
        .withSource(from);

      client.sendEmail(request);
      logger.log("Email forwarded to " + recipient);
    } catch (Exception ex) {
      logger.log("The email was not sent. Error message: " + ex.getMessage());
    }
  }
}
