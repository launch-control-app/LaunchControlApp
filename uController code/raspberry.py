import serial
# import bluetooth
# #import RPi.GPIO as GPIO
# #import time

# def receiveMessages():
#     server_sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )

#     port = 1
#     server_sock.bind(("",port))
#     server_sock.listen(1)

#     client_sock,address = server_sock.accept()
#     print("Accepted connection from " + str(address))

#     data = client_sock.recv(1024)
#     print("received [%s]" % data)

#     client_sock.close()
#     server_sock.close()
  
# def sendMessageTo(targetBluetoothMacAddress, message="hello"):
#     port = 1
#     sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
#     try:
#         sock.connect((targetBluetoothMacAddress, port))
#     except:
#         print("sock.connect failed")

#     try:
#         sock.send(message)
#     except:
#         print("sock.send failed")

#     sock.send(message)
#     sock.close()
  
# def lookUpNearbyBluetoothDevices():
#     nearby_devices = bluetooth.discover_devices()
#     for bdaddr in nearby_devices:
#         try:
#             print(str(bluetooth.lookup_name( bdaddr )) + " [" + str(bdaddr) + "]")
#         except:
#             print(" [" + str(bdaddr) + "]")
#         if (str(bdaddr) == "AC:37:43:D7:CB:DC"):
#             print("Found!")
#             return True 

#     return False


# ser_arduino = serial.Serial(port="/dev/ttyS0", baudrate=9600, timeout=10)
# print("hello world")
# while True:
#     #while not lookUpNearbyBluetoothDevices():
#         #pass

#     txt = ser_arduino.readline()
#     print(txt)
#     sendMessageTo("AC:37:43:D7:CB:DC", txt)

import bluetooth

server_sock= bluetooth.BluetoothSocket( bluetooth.RFCOMM )
server_sock.bind(("",bluetooth.PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

# Start advertising the service
bluetooth.advertise_service(server_sock, "RaspiBtSrv",
                   service_id=uuid,
                   service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
                   profiles=[bluetooth.SERIAL_PORT_PROFILE])
                   
print("Waiting for connection on RFCOMM channel %d" % port)

# client_sock, client_info = server_sock.accept()
# print("Accepted connection from ", client_info)
ser_arduino = serial.Serial(port="/dev/ttyS0", baudrate=9600, timeout=10)

while 1:
    client_sock, client_info = server_sock.accept()
    print("Accepted connection from ", client_info)
    try:
        while True:
            #data = client_sock.recv(1024)
            msg = ser_arduino.readline()
            client_sock.send(msg)
            print("sent {0}".format(msg))
    except IOError:
        pass
    print("disconnected")
    # client_sock.close()

client_sock.close()
server_sock.close()
print("all done")

