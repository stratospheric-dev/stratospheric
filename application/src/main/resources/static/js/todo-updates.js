let stompClient = null;

function connectToWebSocketEndpoint(email) {
  const socket = new SockJS('/websocket');

  stompClient = Stomp.over(socket);
  stompClient.connect({}, () => {
    stompClient.subscribe('/topic/todoUpdates', function (message) {
      $('#message').html(message.body);
      $('#toast').toast('show');
    });

    if (email) {
      stompClient.subscribe('/topic/todoUpdates/' + email, function (message) {
        $('#message').html(message.body);
        $('#toast').toast('show');
      });
    }
  });
}

function disconnectFromWebSocketEndpoint() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }
}

$(document).ready(function () {
  $('#toast').toast({delay: 10000});
});
