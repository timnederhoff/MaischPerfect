# MaischPerfect
Automated maisch program for beer brewing using Raspberry Pi 3 and DS18B20 sensor

## Features
* maisch schema in graph, dynamically match the temperature run
* live temperature shown in gauge
* slope indicator
* indicator for heater switch

## TODO's
* chart: add buttons to toggle series and plotlines
* add alarms
* controls to stop/pause brewprocess
* functionality to load, create, edit and store maisch schemas on the fronend
* logging to frontend
* implement database
* store historical data

To Run Maisch Perfect, add this line to raspberry's /boot/config.txt:

```
dtoverlay=w1-gpio
```

## Wifi settings
* wifi ssid = brewpi
* wifi pass = raspberry
* brewpi ip = 172.24.1.1