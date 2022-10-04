
--
-- Create database MapDB
--
DROP DATABASE IF EXISTS MapDB
CREATE DATABASE MapDB;

--
-- Create user MapUSer and its privileges
--

DROP USER 'MapUser'@'localhost';
CREATE USER 'MapUser' @'localhost' IDENTIFIED BY 'map';
GRANT SELECT ON MapDB.* TO MapUser@localhost;

--
-- Table structure for table 'provaC'
--

DROP TABLE IF EXISTS 'provaC';
CREATE TABLE MapDB.provaC (

    X VARCHAR(10),
    Y FLOAT(5 , 2 ),
    C FLOAT(5 , 2 )
    
);

--
-- Dumping data for table 'provaC'
--

insert into MapDB.provaC values('A',2,1);
insert into MapDB.provaC values('A',2,1);
insert into MapDB.provaC values('A',1,1);
insert into MapDB.provaC values('A',2,1);
insert into MapDB.provaC values('A',5,1.5);
insert into MapDB.provaC values('A',5,1.5);
insert into MapDB.provaC values('A',6,1.5);
insert into MapDB.provaC values('B',6,10);
insert into MapDB.provaC values('A',6,1.5);
insert into MapDB.provaC values('A',6,1.5);
insert into MapDB.provaC values('B',10,10);
insert into MapDB.provaC values('B',5,10);
insert into MapDB.provaC values('B',12,10);
insert into MapDB.provaC values('B',14,10);
insert into MapDB.provaC values('A',1,1);

commit;