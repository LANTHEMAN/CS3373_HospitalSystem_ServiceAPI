Create Table ServiceRequest(
id int Primary Key,
type varchar(50),
firstName varchar(50),
lastName varchar(50),
location varchar(50),
instructions varchar(200),
priority int,
status varchar(50),
completedBy varchar(20),
createdOn TIMESTAMP,
started TIMESTAMP,
completed TIMESTAMP,
destNodeID char(10),
sourceNodeID char(10)
);
