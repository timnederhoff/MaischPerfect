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
    var socket = new SockJS('/maisch-perfect-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/temps', function (temps) {
            chart.series[0].setData(JSON.parse(temps.body).templog);
            chart.series[1].setData(JSON.parse(temps.body).maischmodel);
            var heaterlog = JSON.parse(temps.body).heaterlog;
            var plotLines = { plotLines: []};
            for (var i in heaterlog) {
                var label = heaterlog[i][1] ? 'Heater ON' : 'Heater OFF';
                var lineColor = heaterlog[i][1] ? 'red' : 'blue';
                plotLines.plotLines.push({
                    color: lineColor,
                    dashStyle: 'solid',
                    value: heaterlog[i][0],
                    width: 2,
                    label: {
                        text: label
                    }
                });
            }
            chart.xAxis[0].update(plotLines);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/templog", {}, JSON.stringify({'message': $("#name").val()}));
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});


$(function () {
    chart = new Highcharts.chart('container', {
        title: {
            text: 'Maisch Temperature Process',
            x: -20 //center
        },
        subtitle: {
            text: 'Live Progress of the temperature',
            x: -20
        },
        yAxis: {
            title: {
                text: 'Temperature (°C)'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        xAxis: {
            title: 'Time Units',
            plotLines: []
        },
        tooltip: {
            valueSuffix: '°C'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: [{
            name: 'Measured Temperature',
            data: []
        },{
            name: 'Maisch Schema',
            data: []
        }]
    });
});