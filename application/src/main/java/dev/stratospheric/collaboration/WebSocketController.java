package dev.stratospheric.collaboration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebSocketController {

  private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class.getName());

  // TODO: Make sharing Todo updates via WebSockets work in a load-balanced environment (see https://github.com/stratospheric-dev/stratospheric/issues/42)
  @MessageMapping("/updateTodo")
  @SendTo("/topic/todoUpdates")
  @ResponseBody
  public String receiveNotification(String subject, String message) {
    LOG.info("Relayed todo update. Subject '{}': {}", subject, message);

    return message;
  }
}
