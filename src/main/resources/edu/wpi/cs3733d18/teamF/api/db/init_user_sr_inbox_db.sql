CREATE TABLE Inbox(
username varchar(20) REFERENCES HUser(username),
requestID int REFERENCES ServiceRequest(id)
);