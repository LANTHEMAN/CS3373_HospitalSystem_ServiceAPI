Create Table ServiceRequest (
  id           int Primary Key,
  type         varchar(50),
  nodeID       varchar(15) References Node (id),
  instructions varchar(200),
  priority     int
);
