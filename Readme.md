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

* _id_ tag must be a number and unique - You can add by hand but make sure its not in use!
* _name_ is a name of the device
* _ip_ ip address of device
* _port_ what port is the android log forward service running on
* _filename_ what to call the downloaded log file.

## settings.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<settings>
	<setting>
		<id>0</id>
		<name>Isometric world</name>
		<buffer>20971520</buffer>
		<serverport>1088</serverport>
		<storagepath>F:\Code\logs\isoworld\</storagepath>
		<filenamepath>IsometricWorld.log</filenamepath>
		<sdcard>true</sdcard>
		<devices>
			<device>0</device>
			<device>1</device>
			<device>2</device>
		</devices>
	</setting>
</settings>
```

* _id_ tag must be a number and unique - You can add by hand but make sure its not in use!
* _name_ Name of profile
* _buffer_ size of buffers
* _serverport_ default port the server operates on
* _storagepath_ where should the logs be saved
* _filenamepath_ name of log file to collect from devices
* _sdcard_ is the log file store in an external location on device
* _devices_ what devices use this profile

## Running!

First you need to compile the client program to a jar (Easy to do with eclipse). I called mine LogForwardClient-dependencies.jar  Or you can grab the LogForwardLib and LogForwardClient repo and use ant release.

```
java -jar LogForwardClient-dependencies.jar -dl X:\pathto\devices.xml -s x:\pathto\settings.xml
```

**Remember calling -h will bring up the help screen, this can be done for loading the program, executing normal commands or commands in the modes**

**Remember you have to call -checkout SETTINGPROFILEID before you can run commands** 
Then you have the following options.

> -ca or -collectall

Collect from all the devices

> -cs DEVICEID or -collectsingle DEVICEID

Replace DEVICEID with the id of device you wish to collect from

> -dl or -delall

Delete log from all devices

> -ds DEVICEID or -delsingle DEVICEID

Replace DEVICEID with the id of device you wish to delete from

> -sda or -shutdownall

Attempt to shutdown the service on all devices

> sds DEVICEID or -shutdownsingle DEVICEID

Replace DEVICEID with the id of device you wish to shut down service on

> -checkout SETTINGPROFILEID

Checkout a setting profile to operate on. replaced SETTINGPROFILEID with the setting you wish to operate from.

> -q or -quit

You can call quit at any time to exit the program or the current mode

##Modes

Calling the following commands will go into their respective modes.

> -devicemode or -settingmode

###Device
If you wish to add or update a device you can do it here. 

To view current devices run
> -ls 

Here is an example adding a device.
> -c -n NAME -filename NAMEOFLOG -p PORT -ip IPADDRESS

* -c Creates a device
* -n Name of the device
* -filename filename to save log as.
* -p the port the service runs on
* -ip address of the client.

You can also run a similar command to update a device, you can pick what ever attribute you want to update in the command.
> -u -deviceid ID -n NAME -filename NAMEOFLOG -p PORT -ip IPADDRESS

* -u Update a device
* -deviceid ID of the device
* -n Name of the device
* -filename filename to save log as.
* -p the port the service runs on
* -ip address of the client.

**Remember to call -w in the mode to write changes!**

###Settings
If you wish to create or update a profile you can do it here.

To view current setting profiles run
> -ls

Here is an example of adding a setting profile
> -c -n NAME -storagepath XYZ -filenamepath XYZ -sdcard TRUEorFALSE

* -c Create a setting profile
* -n Name of setting profile
* -storagepath Location to store files on local disk
* -filenamepath Name and location of where the log file will be located on remote device
* -sdcard is the log on an external sdcard?

You can then update the setting profile if needs be
> -u SETTINGPROFILEID -n NAME -storagepath XYZ -filenamepath XYZ -sdcard TRUEorFALSE -b BUFFERSIZE -p PORT

* -b Size of local network buffer
* -p Default port to use when connecting to remote devices.

You then have to add devices to the setting profile, this is quite easy to do.
> -a SETTINGPROFILEID -did DEVICEID

* -a Add a device to the given setting profile
* -did the device ID to add to profile

**Remember to call -w in the mode to write changes!**
## Future
I've implemented a lot more than I first thought I would. Some issues and ideas are in the issue area.  Working with the command line can make you become bogged down, so a GUI in future would be a good idea!

The Client.java is starting to bloat and the if statements dealing with commands could become fragile in future.

