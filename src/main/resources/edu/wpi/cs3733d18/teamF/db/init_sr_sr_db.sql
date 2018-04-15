Create Table SecurityRequest(
username varchar(20) REFERENCES HUser(username)
);