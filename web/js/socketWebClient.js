/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var output = document.getElementById("output");
var button = document.getElementById("testSocket");
var msgBtn = document.getElementById("testCheer");
var websocket;

button.addEventListener("click", (e)=>connectToBackend());

function onOpen(event){
    console.log(event);
    output.innerHTML += "<p>Socket connected!</p>";
    msgBtn.removeEventListener("click", sendTestCheer);
    msgBtn.addEventListener("click", sendTestCheer);
}

function sendTestCheer(){
    sendMessage(JSON.stringify({type:"cheer", team: "A"}).toString());
}

function onError(event) {
    console.log(event);   
    writeToScreen('<span style="color: red;">ERROR:</span> ' + event.data);
}

function onMessage(event){
    console.log(event);
    let json = JSON.parse(event.data);
    output.innerHTML += "<p>New message from server: "+ json.message + " from "+json.name+"</p>";
}

function onClose(event){    
    output.innerHTML += "<p>Socket disconnected!</p>";
}

function sendMessage(text){
    websocket.send(text);
    output.innerHTML += "<p>Sending "+text+"</p>";
}

function onMatchIdObtained(response){
    console.log(response);
    var wsUri = "ws://" + document.location.host + document.location.pathname + "cheer/"+response+"/?name=browserclient";
    websocket = new WebSocket(wsUri);

    websocket.onerror = onError;
    websocket.onopen = onOpen;
    websocket.onmessage = onMessage;
    websocket.onclose = onClose;
}

function connectToBackend(){
    var oReq = new XMLHttpRequest();   
    oReq.onreadystatechange = ()=>{
        if(oReq.readyState === XMLHttpRequest.DONE && oReq.status === 200) {
            onMatchIdObtained(oReq.responseText);   
        }             
    }   
    oReq.open("GET", "http://" + document.location.host + document.location.pathname + "webresources/match/");
    oReq.send();
}
