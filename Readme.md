# Requirements

Please forgive me, its been some months when I wrote this so everything isn't fresh.


Two XML files are required for the program to run

## devices.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<devices>
<!--
	<device>
		<id>0</id>
		<name>Nexus 7</name>
		<ip>192.168.1.9</ip>
		<port>1088</port>
		<filename>Nexus7.log</filename>
	</device>
	-->
	<device>
		<id>3</id>
		<name>Desire</name>
		<ip>192.168.1.10</ip>
		<port>1088</port>
		<filename>Desire.log</filename>
	</device>
	<device>
		<id>1</id>
		<name>Nexus 4</name>
		<ip>192.168.1.44</ip>
		<port>1088</port>
		<filename>Nexus4.log</filename>
	</device>
	<device>
		<id>2</id>
		<name>Desire S</name>
		<ip>192.168.1.28</ip>
		<port>1088</port>
		<filename>DesireS.log</filename>
	</device>
</devices>
```

* _id_ tag must be a number and unique 
* _name_ is a name of the device
* _ip_ ip address of device
* _port_ what port is the android log forward service running on
* _filename_ what to call the downloaded log file.

## settings.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<settings>
	<setting>
		<name>Isometric world</name>
		<buffer>20971520</buffer>
		<serverport>1088</serverport>
		<storagepath>F:\Code\logs\isoworld\</storagepath>
		<filenamepath>IsometricWorld.log</filenamepath>
		<sdcard>true</sdcard>
		<devices>
			<device>Nexus 7</device>
			<device>Nexus 4</device>
			<device>Desire S</device>
		</devices>
	</setting>
</settings>
```

* _name_ Seems redundant
* _buffer_ size of buffers
* _serverport_ default port the server operates on
* _storagepath_ where should the logs be saved
* _filenamepath_ name of log file to collect from devices
* _sdcard_ is the log file store in an external location on device
* _devices_ Seems redundant

## Running!

First you need to compile the client program to a jar (Easy to do with eclipse). I called mine LFCRun.jar 

```
java -jar LFCRun.jar X:\pathto\devices.xml x:\pathto\settings.xml
```

Then you have the following options.

> collect-all

Collect from all the devices

> collect-single

Will print out devices id, enter in id of device you wish to collect from

> del-all

Delete log from all devices

> del-single

Will print out devices id, enter in id of device you wish to delete from

> shutdown-all

Attempt to shutdown the service on all devices

>shutdown-single

Will print out devices id, enter in id of device you wish to shutdown its server.

> quit

You can call quit at any time to exit the program.

> up

You can call up to come out of sub menus (ie XXX-single commands)

## Display
The display is quite messy as it prints out information from the NIO selector thread, so its not very clear if a request was sucessful or not. 

## Future
When I'm fed up with this hodgepodge program I will do the following

* Build with ant
* Use Apache CLI for inputs
* Clean up display output - make it more english!
* Have its own log file, perhaps output more technical info to a log file than what is displayed.