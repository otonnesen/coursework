if [ "$1" == "" ]; then
	echo "No file specified"
else
	cmd.exe /c avrdude.exe -C "C:\Program Files (x86)\Arduino\hardware\tools\avr\etc\avrdude.conf" -p atmega2560 -c wiring -P COM3 -b 115200 -D -F -U flash:w:$1
fi
