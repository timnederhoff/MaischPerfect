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
            var templogs = JSON.parse(temps.body).templog;
            var maischmodel = JSON.parse(temps.body).maischmodel;
            var heaterlog = JSON.parse(temps.body).heaterlog;

            updateSeries(0, templogs);
            updateSeries(1, maischmodel);

            for (var i in heaterlog) {
                var label = heaterlog[i][1] ? 'Heater ON' : 'Heater OFF';
                var lineColor = heaterlog[i][1] ? 'red' : 'blue';
                chart.xAxis[0].addPlotLine({
                    color: lineColor,
                    dashStyle: 'solid',
                    value: heaterlog[i][0],
                    width: 2,
                    label: {
                        text: label
                    }
                }, false);
            }

            chart.redraw();
        });

    });

    requestData();
}

function updateSeries(seriesIndex, seriesContent) {
    if (seriesContent.length == 1) {
        chart.series[seriesIndex].addPoint(seriesContent[0], false);
    } else if (seriesContent.length > 1) {
        for (var i in seriesContent) {
            chart.series[seriesIndex].addPoint(seriesContent[i], false);
        }
    }
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function requestData() {

    setTimeout(function () {
        var frompointTemptmp = Math.max.apply(Math,chart.series[0].data.map(function(o){return o.x;}));
        var frompointMaischtmp = Math.max.apply(Math,chart.series[1].data.map(function(o){return o.x;}));
        var frompointHeater = -1;
        stompClient.send("/app/templog", {}, JSON.stringify({
            "fromPointTemp": frompointTemptmp > 0 ? frompointTemptmp : -1,
            "fromPointMaisch": frompointMaischtmp > 0 ? frompointMaischtmp : -1,
            "fromPointHeater": frompointHeater
        }));
        requestData();
    }, 500);

}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { requestData(); });
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