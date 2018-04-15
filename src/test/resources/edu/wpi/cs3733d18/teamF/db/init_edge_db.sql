Create Table Edge (
edgeID varchar(15) Primary Key ,
startNode varchar(15)  References Node(id),
endNode varchar(15) References Node(id)
);