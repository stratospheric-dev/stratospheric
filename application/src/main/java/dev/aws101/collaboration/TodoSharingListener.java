package dev.aws101.collaboration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class TodoSharingListener {

  private static final Logger LOG = LoggerFactory.getLogger(TodoSharingListener.class.getName());

  @SqsListener(value = "${custom.sharing-queue}")
  public void listenToSharingMessages(TodoCollaborationRequest payload) {
    LOG.info("Incoming todo sharing payload: " + payload);
  }
}
