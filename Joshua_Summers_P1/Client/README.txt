In this project the commands login, shutdown, solve, list, and logout have been implemented.
To build this program either open it in intelliJ or copy the included server file into a new project.
Do the same for the client file and then change the file directory on line 44 of the server to wherever you place the included logins.txt.
It is important that this logins.txt file is used as it is formatted specifically for this program. 
The program also will not run if the file path still points to my computer.
Once the program is running all commands are locked until a successful login is performed.
A login can be done with LOGIN root root22, or any of the users shown in logins.txt. The program is case sensative, the commands LOGIN, SHUTDOWN, SOLVE, LIST, and LOGOUT must be given in uppercase.
The root user can list all solutions by typing LIST -all. Any user can solve with SOLVE -c and SOLVE -r followed by a number or two.

Below is a sample output of all commands from the client side.

Send command to server:	LOGIN root root22
Server says: hello root
Send command to server:	SOLVE -c 5
Server says: Circle's circumference is 31.40 and area is 78.50
Send command to server:	SOLVE -r 3 2
Server says: Rectangle's perimeter is 10.00 and area is 6.00
Send command to server:	LIST
Server says: 
radius 3: Circle's circumference is 18.84 and area is 28.26
radius 5: Circle's circumference is 31.40 and area is 78.50
sides 3 2: Rectangle's perimeter is 10.00 and area is 6.00

Send command to server:	LIST -all
Server says: 
root
radius 3: Circle's circumference is 18.84 and area is 28.26
radius 5: Circle's circumference is 31.40 and area is 78.50
sides 3 2: Rectangle's perimeter is 10.00 and area is 6.00
john
radius 5: Circle's circumference is 31.40 and area is 78.50
sides 4 4: Rectangle's perimeter is 16.00 and area is 16.00
Error: No Sides Found
sides 4 4: Rectangle's perimeter is 16.00 and area is 16.00
sally
No Interactions Yet
qiang
No Interactions Yet

Send command to server:	LOGOUT
Server says: 200 OK

Process finished with exit code 0



Below is the output after I re-logged in to demonstrate the shutdown command.

Send command to server:	LOGIN root root22
Server says: hello root
Send command to server:	SHUTDOWN
Server says: 200 OK

Process finished with exit code 0