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
        var currentTemperatureSubscription = stompClient.subscribe('/topic/livedata', function (currentTemp) {
            tempchart.series[0].points[0].update(parseInt(currentTemp.body));
        });
        var processSubscription = stompClient.subscribe('/topic/temps', function (temps) {
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
        var frompointHeater = Math.max.apply(Math,chart.xAxis[0].plotLinesAndBands.map(function(o){return o.options.value}));
        stompClient.send("/app/templog", {}, JSON.stringify({
            "fromPointTemp": frompointTemptmp > 0 ? frompointTemptmp : -1,
            "fromPointMaisch": frompointMaischtmp > 0 ? frompointMaischtmp : -1,
            "fromPointHeater": frompointHeater > 0 ? frompointHeater : -1
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

$(function () {
    tempchart = new Highcharts.chart('tempgauge', {

        chart: {
            type: 'gauge',
            plotBackgroundColor: null,
            plotBackgroundImage: null,
            plotBorderWidth: 0,
            plotShadow: false
        },

        title: {
            text: 'Current Temperature'
        },

        pane: {
            startAngle: -150,
            endAngle: 150,
            background: [{
                backgroundColor: {
                    linearGradient: {x1: 0, y1: 0, x2: 0, y2: 1},
                    stops: [
                        [0, '#FFF'],
                        [1, '#333']
                    ]
                },
                borderWidth: 0,
                outerRadius: '109%'
            }, {
                backgroundColor: {
                    linearGradient: {x1: 0, y1: 0, x2: 0, y2: 1},
                    stops: [
                        [0, '#333'],
                        [1, '#FFF']
                    ]
                },
                borderWidth: 1,
                outerRadius: '107%'
            }, {
                // default background
            }, {
                backgroundColor: '#DDD',
                borderWidth: 0,
                outerRadius: '105%',
                innerRadius: '103%'
            }]
        },

        // the value axis
        yAxis: {
            min: 0,
            max: 120,

            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',

            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 10,
            tickColor: '#666',
            labels: {
                step: 2,
                rotation: 'auto'
            },
            title: {
                text: '°C'
            },
            plotBands: [{
                from: 0,
                to: 40,
                color: '#55BF3B' // green
            }, {
                from: 40,
                to: 80,
                color: '#DDDF0D' // yellow
            }, {
                from: 80,
                to: 120,
                color: '#DF5353' // red
            }]
        },

        series: [{
            name: 'temperature',
            data: [70],
            tooltip: {
                valueSuffix: ' °C'
            }
        }]
    });

});