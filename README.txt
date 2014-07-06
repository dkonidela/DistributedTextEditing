Distributed Real Time Editing System

1.Start the Server and know the IP address of server.
2.Start the first Client and get connected to Sever with its IP address.
3.create a New Document.
4.Start another peer at different Machine.
5.Use view command at peer 2 to see the document information.
6.Use join command at peer 2 to join.
7.If no one is editing and the document is exsisting, use Open command.
8.Use closeDoc button in the GUI to close document.
9.leave to get disconnected from the Server.


Commands List
connect
create
view
join <filenamewithoutextension>
open <filenamewithoutextension>
leave

Note:
1.open command is used only if the document is exsisting at server and no one is currently editing it.(iscurrentlyinuse=false, when view is used).
2.Once a document is opened all others are joined using join command.(iscurrentlyinuse=true, when view is done)
