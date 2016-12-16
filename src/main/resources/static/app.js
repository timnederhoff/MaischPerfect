var stompClient = null;

$( document ).ready(function() {
    connect();
    //here: check if a process is already started
});

function connect() {
    var socket = new SockJS('/maisch-perfect-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        var liveDataSubscription = stompClient.subscribe('/topic/livedata', function (currentTemp) {
            tempchart.series[0].points[0].update(parseInt(currentTemp.body));
        });
        var processSubscription = stompClient.subscribe('/topic/temps', function (temps) {
            var templogs = JSON.parse(temps.body).templog;
            var maischmodel = JSON.parse(temps.body).appliedModel;
            var heaterlog = JSON.parse(temps.body).heaterlog;

            $( "#slope" ).text(JSON.parse(temps.body).slope);

            addToSeries(1, templogs);
            chart.series[0].setData(maischmodel);

            for (var i in heaterlog) {
                chart.xAxis[0].addPlotLine(heaterlog[i], false);
            }

            chart.redraw();

            setTimeout(function () {
                if (!JSON.parse(temps.body).ended) {
                    requestData()
                }
            }, 1500);

        });

    });
}

function requestData() {
    var frompointTemptmp = Math.max.apply(Math,chart.series[1].data.map(function(o){return o.x;}));
    var frompointHeater = Math.max.apply(Math,chart.xAxis[0].plotLinesAndBands.map(function(o){return o.options.value}));
    stompClient.send("/app/templog", {}, JSON.stringify({
        "fromPointTemp": frompointTemptmp > 0 ? frompointTemptmp : -1,
        "fromPointMaisch": -1,
        "fromPointHeater": frompointHeater > 0 ? frompointHeater : -1
    }));
}

function addToSeries(seriesIndex, seriesContent) {
    if (seriesContent.length == 1) {
        chart.series[seriesIndex].addPoint(seriesContent[0], false);
    } else if (seriesContent.length > 1) {
        for (var i in seriesContent) {
            chart.series[seriesIndex].addPoint(seriesContent[i], false);
        }
    }
}

function startProcess() {
    $.get( "start", function( data ) {
        console.log("brew prcess started");
    });
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#start" ).click(function () {
        startProcess();
        requestData();
    })
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
            plotLines: [],
            type: 'datetime'
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
            name: 'Maisch Schema',
            data: []
        },{
            name: 'Measured Temperature',
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
            }],
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
            data: [0],
            tooltip: {
                valueSuffix: ' °C'
            }
        }]
    });

});