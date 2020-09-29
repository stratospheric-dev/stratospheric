let stompClient = null;

function connect() {
  const socket = new SockJS('/websocket');

  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    console.log('Connection to WebSocket endpoint was successful: ' + frame);

    stompClient.subscribe('/topic/todoUpdates', function (message) {
      console.log(message);
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
