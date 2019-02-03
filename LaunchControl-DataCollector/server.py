import bluetooth
import random
import time

def getString():
    data = list()
    for i in range(19):
        data.append(str(random.randint(0, 255)))
    return str(",".join(data) + "\n")

server_socket= bluetooth.BluetoothSocket(bluetooth.RFCOMM)

server_socket.bind(("", bluetooth.PORT_ANY))
server_socket.listen(1)


uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"

# Start advertising the service
bluetooth.advertise_service(server_socket, "RaspiBtSrv",
                   service_id=uuid,
                   service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
                   profiles=[bluetooth.SERIAL_PORT_PROFILE])

print("Now listening...")
while True:
    print("Now listening part 1...")
    client_socket, address = server_socket.accept()
    print("Now listening part 2...")
    while True:
        print("found connections")
        try:
            print("got here")
            client_socket.send(getString())
            time.sleep(0.5)
        except:
            print('something happend')
            client_socket.close()
            break


server_socket.close()



