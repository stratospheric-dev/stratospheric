package dev.stratospheric.email;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.apache.commons.mail.util.MimeMessageParser;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ForwardingHandler implements RequestHandler<S3Event, Void> {

  private static final String EMAIL_TOM = System.getenv("EMAIL_TOM");
  private static final String EMAIL_BJOERN = System.getenv("EMAIL_BJOERN");
  private static final String EMAIL_PHILIP = System.getenv("EMAIL_PHILIP");

  private LambdaLogger logger;

  private final AmazonS3 s3Client = AmazonS3ClientBuilder
    .defaultClient();

  private final AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder
    .standard()
    .withRegion(Regions.EU_WEST_1)
    .build();

  @Override
  public Void handleRequest(S3Event s3Event, Context context) {

    this.logger = context.getLogger();

    var bucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
    var key = s3Event.getRecords().get(0).getS3().getObject().getKey();

    logger.log("New E-Mail received: " + bucket + "/" + key);

    try {
      var messageParser = new MimeMessageParser(new MimeMessage(null, s3Client.getObject(bucket, key).getObjectContent()));
      messageParser = messageParser.parse();

      var plainContent = messageParser.getPlainContent();
      var htmlContent = messageParser.getHtmlContent();
      var from = messageParser.getFrom();

      var subject = "Forwarded (Stratospheric) Mail: " + messageParser.getSubject();

      var receivers = new ArrayList<Address>();

      receivers.addAll(messageParser.getTo());
      receivers.addAll(messageParser.getCc());
      receivers.addAll(messageParser.getBcc());

      logger.log("Potential receivers of this email are: " + Arrays.toString(receivers.toArray()));

      var forwardingRecipients = new HashSet<String>();

      for (Address address : receivers) {
        var emailAddress = address.toString();
        if (emailAddress.contains("info@stratospheric.dev")) {
          forwardingRecipients.add(EMAIL_BJOERN);
          forwardingRecipients.add(EMAIL_PHILIP);
          forwardingRecipients.add(EMAIL_TOM);
        }

        if (emailAddress.contains("bjoern@stratospheric.dev")) {
          forwardingRecipients.add(EMAIL_BJOERN);
        }

        if (emailAddress.contains("philip@stratospheric.dev")) {
          forwardingRecipients.add(EMAIL_PHILIP);
        }

        if (emailAddress.contains("tom@stratospheric.dev")) {
          forwardingRecipients.add(EMAIL_TOM);
        }
      }

      var emailContent = constructEmailContent(plainContent, htmlContent, subject);

      for (var forwardingRecipient : forwardingRecipients) {
        sendEmail(emailContent, forwardingRecipient, from);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  private Message constructEmailContent(String plainContent, String htmlContent, String subject) {

    var message = new Message()
      .withBody(new Body())
      .withSubject(new Content().withCharset(UTF_8.name()).withData(subject));

    if (plainContent == null && htmlContent == null) {
      message.getBody()
        .setText(new Content()
          .withCharset(UTF_8.name())
          .withData("Neither HTML nor plain/text provided in the E-Mail. Please check our S3 bucket for the raw email"));

      return message;
    }

    if (htmlContent != null) {
      message.getBody()
        .setHtml(new Content()
          .withCharset(UTF_8.name())
          .withData(htmlContent)
        );
    }

    if (plainContent != null) {
      message.getBody()
        .setText(new Content()
          .withCharset(UTF_8.name())
          .withData(plainContent)
        );
    }

    return message;
  }

  private void sendEmail(Message emailContent, String recipient, String from) {
    try {
      var sendEmailRequest = new SendEmailRequest()
        .withDestination(
          new Destination().withToAddresses(recipient))
        .withMessage(emailContent)
        .withSource("noreply@stratospheric.dev")
        .withReplyToAddresses(from);

      sesClient.sendEmail(sendEmailRequest);

      logger.log("Email forwarded to " + recipient);
    } catch (Exception ex) {
      logger.log("The email was not sent. Error message: " + ex.getMessage());
      throw new RuntimeException("Failed to forward an incoming email - failing the Lambda to identify an error");
    }
  }
}
