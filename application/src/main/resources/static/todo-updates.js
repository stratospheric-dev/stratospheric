let stompClient = null;

function connect() {
  const socket = new SockJS('/websocket');

  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    console.log('Connection to WebSocket endpoint was successful: ' + frame);

    stompClient.subscribe('/topic/todoUpdates', function (message) {
      console.log(message);

      $('#message').html(message.body);
      $('#toast').toast('show');
    });
  });
}

function disconnect() {
  if (stompClient !== null) {
    stompClient.disconnect();
  }

  console.log("Disconnected from WebSocket endpoint.");
}

connect();

$(document).ready(function () {
  $('#toast').toast({delay: 5000});
});
