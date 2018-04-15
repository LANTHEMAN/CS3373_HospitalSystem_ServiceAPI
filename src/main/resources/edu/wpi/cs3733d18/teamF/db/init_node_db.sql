Create Table Node (
  id        char(10) Primary Key,
  x_coord   REAL not NULL,
  y_coord   REAL not NULL,
  floor     char(2),
  building  varchar(30),
  nodeType  char(4),
  longName  varchar(60),
  shortName varchar(50),
  teamName  varchar(30),
  xcoord3d  REAL not NULL,
  ycoord3d  REAL not NULL
)