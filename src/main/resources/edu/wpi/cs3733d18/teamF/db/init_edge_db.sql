Create Table Edge (
  edgeID    char(21) Primary Key,
  startNode char(10) References Node (id),
  endNode   char(10) References Node (id)
);