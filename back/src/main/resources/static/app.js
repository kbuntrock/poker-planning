var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/websocket', null, {transports:['websocket','eventsource','xhr-polling']});
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/user/topic/error', function (errorText) {
            showText("<b style='color:red'> Error : " + errorText.body + "</b>");
        });
    }, function (error) {
        console.log('Hum... problème... ' + error);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function enterSession() {
    stompClient.subscribe('/topic/planning/' + $("#sesId").val(), function (msg) {
            showText(JSON.parse(msg.body));
     });
}

function register() {
    stompClient.send("/app/planning/" + $("#sesId").val() + "/register", {}, JSON.stringify({'name': $("#name").val()}));
}

function newStory() {
    stompClient.send("/app/planning/" + $("#sesId").val() + "/newStory", {}, $("#storyName").val());
}

function vote() {
    stompClient.send("/app/planning/" + $("#sesId").val() + "/vote", {}, JSON.stringify({'value': $("#votePoint").val()}));
}

function reveal() {
    stompClient.send("/app/planning/" + $("#sesId").val() + "/reveal", {});
}

function showText(message) {
    $("#greetings").append("<tr><td>" + JSON.stringify(message) + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#enterSession" ).click(function() { enterSession(); });
    $( "#register" ).click(function() { register(); });
    $( "#newStory" ).click(function() { newStory(); });
    $( "#vote" ).click(function() { vote(); });
    $( "#reveal" ).click(function() { reveal(); });
});

