SET SQL_SAFE_UPDATES = 0;
DROP DATABASE IF EXISTS DENGOHOTELRESERVATION;
CREATE DATABASE DENGOHOTELRESERVATION;
USE DENGOHOTELRESERVATION;


DROP TABLE IF EXISTS BEDTYPE;
CREATE TABLE BEDTYPE
(
bedTypeID INT AUTO_INCREMENT,
bedName VARCHAR(50) DEFAULT NULL,
reserved BOOLEAN DEFAULT FALSE,
price INT,
PRIMARY KEY(bedTypeID)
);


DROP TABLE IF EXISTS ROOMTYPE;
CREATE TABLE ROOMTYPE
(
rTypeID INT AUTO_INCREMENT,
max INT,
PRIMARY KEY(rTypeID)
);


DROP TABLE IF EXISTS CUSTOMER;
CREATE TABLE CUSTOMER
(
cID INT AUTO_INCREMENT,
fName VARCHAR(30),
lName VARCHAR(30),
addrST VARCHAR(50)DEFAULT NULL,
country VARCHAR(30) DEFAULT NULL,
city VARCHAR(30) DEFAULT NULL,
zipCode INT DEFAULT NULL,
phone INT DEFAULT NULL,
email VARCHAR(50) DEFAULT NULL,
dob DATE DEFAULT '0000-00-00',
PRIMARY KEY(cID)
);


DROP TABLE IF EXISTS ROOM;
CREATE TABLE ROOM
(
roomID INT AUTO_INCREMENT,
rName VARCHAR(50) DEFAULT NULL,
bedTypeID INT,
rTypeID INT,
reserved BOOLEAN DEFAULT FALSE,
price INT,
PRIMARY KEY(roomID),
FOREIGN KEY (bedTypeID) references BedType (bedTypeID),
FOREIGN KEY (rTypeID) references RoomType (rTypeID)
);


DROP TABLE IF EXISTS RESERVATION;
CREATE TABLE RESERVATION
(
rID INT AUTO_INCREMENT,
cID INT,
roomID INT,
dateIN DATE,
dateOUT DATE,
payDUE DATE,
payAMT INT,
paid BOOLEAN,
updatedAt TIMESTAMP,
PRIMARY KEY(rID),
FOREIGN KEY (cID) references Customer (cID),
FOREIGN KEY (roomID) references Room (roomID)
);


DROP TABLE IF EXISTS PAYMENT;
CREATE TABLE PAYMENT
(
pID INT AUTO_INCREMENT,
rID INT,
cID INT,
method VARCHAR(30),
amount INT,
comments VARCHAR(50),
PRIMARY KEY(pID),
FOREIGN KEY (rID) references Reservation (rID),
FOREIGN KEY (cID) references Customer (cID)
);


DROP TABLE IF EXISTS ARCHIVE;
CREATE TABLE ARCHIVE
(
rID INT AUTO_INCREMENT,
cID INT,
roomID INT,
dateIN DATE,
dateOUT DATE,
payDUE DATE,
payAMT INT,
paid BOOLEAN,
updatedAt TIMESTAMP,
PRIMARY KEY(rID),
FOREIGN KEY (cID) references Customer (cID),
FOREIGN KEY (roomID) references Room (roomID)
);


DROP TRIGGER IF EXISTS resCanceled
delimiter //
CREATE TRIGGER resCanceled
AFTER DELETE ON RESERVATION
FOR EACH ROW
BEGIN
    UPDATE ROOM
    SET reserved = FALSE
    WHERE OLD.roomID = roomID;
END;
//
delimiter ;

DROP PROCEDURE IF EXISTS archiveReservations;
delimiter //
CREATE PROCEDURE archiveReservations(IN d TIMESTAMP)
BEGIN

INSERT INTO Archive
SELECT * from Reservation where updatedAt < d;

DELETE FROM Reservation where updatedAt < d;
END;
//
delimiter ;


DROP TRIGGER IF EXISTS resAdd
delimiter //
CREATE TRIGGER resAdd
AFTER INSERT ON RESERVATION
FOR EACH ROW
BEGIN
    UPDATE ROOM
    SET reserved = TRUE
    WHERE NEW.roomID = roomID;
END;
//
delimiter ;

LOAD DATA LOCAL INFILE 'c:\\Users\\Wilson\\Documents\\CS157a\\project\\bedtype.txt' INTO TABLE BEDTYPE;
LOAD DATA LOCAL INFILE 'c:\\Users\\Wilson\\Documents\\CS157a\\project\\roomtype.txt' INTO TABLE ROOMTYPE;
LOAD DATA LOCAL INFILE 'c:\\Users\\Wilson\\Documents\\CS157a\\project\\customer.txt' INTO TABLE CUSTOMER;
LOAD DATA LOCAL INFILE 'c:\\Users\\Wilson\\Documents\\CS157a\\project\\room.txt' INTO TABLE ROOM;
LOAD DATA LOCAL INFILE 'c:\\Users\\Wilson\\Documents\\CS157a\\project\\reservation.txt' INTO TABLE RESERVATION;
LOAD DATA LOCAL INFILE 'c:\\Users\\Wilson\\Documents\\CS157a\\project\\payment.txt' INTO TABLE PAYMENT;
LOAD DATA LOCAL INFILE 'c:\\Users\\Wilson\\Documents\\CS157a\\project\\archive.txt' INTO TABLE ARCHIVE;
