/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function onOpen(event){
    console.log(event);
}

function onError(event) {
    console.log(event);
    writeToScreen('<span style="color: red;">ERROR:</span> ' + event.data);
}

function onMessage(event){
    console.log(event);
}

function onClose(event){
    console.log(event);
}

function sendMessage(text){
    websocket.send(text);
}

var wsUri = "ws://" + document.location.host + document.location.pathname + "cheer/";
var websocket = new WebSocket(wsUri);

websocket.onerror = onError;
websocket.onopen = onOpen;
websocket.onmessage = onMessage;
websocket.onclose = onClose;

