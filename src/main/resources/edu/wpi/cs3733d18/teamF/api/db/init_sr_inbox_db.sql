Create Table Inbox(
username varchar(50),
requestID int REFERENCES SERVICEREQUEST(ID)
);