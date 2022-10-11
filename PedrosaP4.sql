drop database CS331;
create database CS331;
use CS331;
#creates School
create table school(schoolName varchar(30) NOT NULL UNIQUE,
    state char(2),
    points int default 0,
    primary key(schoolName));

#creates athlete
create table Athlete(compID int  auto_increment NOT NULL unique ,
    firstName varchar(20),
    lastName varchar(20),
    gender char,
    schoolName varchar(30),
    moreThan4 boolean,
    primary key (compID) ,
    foreign key (schoolName)
                    REFERENCES school(schoolName)
                    ON DELETE RESTRICT);

#create event
create table Events(eventNumber int NOT NULL UNIQUE,
    eventType varchar(5),
    eventName varchar(15),
    eventGender char,
    primary key (eventNumber));

#create results;
create table Results(results double,
    placement int default NULL,
    dq boolean DEFAULT false,
    compID int,
    eventNumber int,
    entryNumber int auto_increment unique,
    points int DEFAULT null,
    primary key (entryNumber),
    foreign key (compID)
                    REFERENCES Athlete (compID)
                    ON DELETE RESTRICT,
    foreign key (eventNumber)
                    REFERENCES Events (eventNumber)
                     ON DELETE RESTRICT);


#Relay Results
create table RelayResults(results double,
    placement int,
    dq boolean DEFAULT false,
    starter int,
    runner2 int,
    runner3 int,
    anchor int,
    eventNumber int,
    gender char,
    foreign key (starter)
                    REFERENCES Athlete (compID)
                    ON DELETE RESTRICT,
    foreign key (runner2)
                    REFERENCES Athlete (compID)
                    ON DELETE RESTRICT,
    foreign key (runner3)
                    REFERENCES Athlete (compID)
                    ON DELETE RESTRICT,
    foreign key (anchor)
                    REFERENCES Athlete (compID)
                    ON DELETE RESTRICT,
    foreign key (eventNumber)
                    REFERENCES Events (eventNumber)
                     ON DELETE RESTRICT);


#SameName
delimiter //
create trigger sameName before insert on Athlete for each row
    begin declare firstLast varchar(255);
    set firstLast = concat("The name ",NEW.firstName," " ,new.lastName, " Already Exists");
    if(select count(*) from Athlete where firstName = new.firstName and lastName = new.lastName and schoolName = new.schoolName > 0)
        then
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = firstLast;
end if;
end //

#Dont touch compID
delimiter //
create trigger staticCompID  before update on Athlete for each row
    begin declare error varchar(60);
    set error = concat('You cannot change Competitors numbers once assigned!');
    if old.compID != new.compID
        then SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = error;
END IF;
end //

insert into school value ("Eastern Oregon University","OR", 0); #1
insert into school value ("Oregon State University","OR", 0); #2
insert into school value ("Oregon Institute of Technology","OR", 0);#3
insert into school value ("Boise State","ID", 0); #4
insert into school value ("University of Washigton","WA", 0);#5

insert into Athlete value (100, "Joe", "Hardwood", "F", "Oregon State University",false); #1
insert into Athlete value (101, "Cali", "Hardpillow", "F", "Oregon State University",false); #2
insert into Athlete value (102, "Betsy", "Knight", "F", "Oregon State University",false); #3
insert into Athlete value (103, "Bob", "Nitt", "M", "Oregon State University",false); #1
insert into Athlete value (104, "Sarah", "Bite", "M", "Oregon State University",false); #2
insert into Athlete value (105, "Betty", "Kight", "M","Oregon State University",false); #3

insert into Athlete value (106, "Bobby", "kit", "F", "Eastern Oregon University",false); #1
insert into Athlete value (107, "Bilord", "Load", "F", "Eastern Oregon University",false); #2
insert into Athlete value (108, "Emma", "High", "F", "Eastern Oregon University",false); #3
insert into Athlete value (109, "Ward", "Vility", "M", "Eastern Oregon University",false); #1
insert into Athlete value (110, "Ema", "Yo", "M", "Eastern Oregon University",false); #2
insert into Athlete value (111, "Empon", "Hoes", "M", "Eastern Oregon University",false); #3

insert into Athlete value (112, "Elloise", "Loss", "F", "University of Washigton",false); #1
insert into Athlete value (113, "Looso", "Notty", "F", "University of Washigton",false); #2
insert into Athlete value (114, "Killierd", "Porptz", "F", "University of Washigton",false); #3
insert into Athlete value (115, "Kingong", "Hoelong", "M", "University of Washigton",false); #1
insert into Athlete value (116, "Bonghong", "Yong", "M", "University of Washigton",false); #2
insert into Athlete value (117, "Loiodd", "Hidnd", "M", "University of Washigton",false); #3

insert into Athlete value (118, "ABoci", "Hifnd", "M","Boise State",false); #1
insert into Athlete value (119, "Bidnf", "Hoewsns", "M","Boise State",false); #2
insert into Athlete value (120, "Loidyling", "Kashoot", "M","Boise State",false); #3
insert into Athlete value (121, "Linda", "Killyt", "F","Boise State",false); #1
insert into Athlete value (122, "Lylee", "Koshoot", "F","Boise State",false); #2
insert into Athlete value (123, "Lolla", "Kilms", "F","Boise State",false); #3

insert into Athlete value (124, "Isac", "Kilms", "M","Oregon Institute of Technology",false); #1
insert into Athlete value (125, "Pilop", "Kelm", "M","Oregon Institute of Technology",false); #2
insert into Athlete value (126, "Justin", "Echo", "M","Oregon Institute of Technology",false); #3
insert into Athlete value (127, "Lila", "Alzse", "F","Oregon Institute of Technology",false); #1
insert into Athlete value (128, "Abby", "Bone", "F","Oregon Institute of Technology",false); #2
insert into Athlete value (129, "Echo", "Alig", "F","Oregon Institute of Technology",false); #3

insert into Events value (1, "Track", "1500 Meters", "F"); #1
insert into Events value (2, "Track", "3000 Steepchase","F"); #2
insert into Events value (3, "Track", "110 Hurdles", "F"); #3
insert into Events value (4, "Track", "100 Meters", "F"); #4
insert into Events value (5, "Track", "400 Meters", "F"); #5

insert into Events value (6, "Track", "800 Meters", "M"); #6
insert into Events value (7, "Track", "400 Hurdles", "M"); #7
insert into Events value (8, "Track", "200 Meters", "M"); #8
insert into Events value (9, "Field", "Hammer", "M"); #9
insert into Events value (10, "Field", "Javelin", "M"); #10

insert into Results value (144, null, false, 100, 1, NULL,null);
insert into Results value (146, null, false, 101, 2, NULL,null);
insert into Results value (49.9,null , false, 102, 3, NULL,null);

insert into Results value (49.7, null, false, 103, 6, NULL,null);
insert into Results value (10, null, false, 104, 7, NULL,null);
insert into Results value (9.88, null, false, 105, 8, NULL,null);

insert into Results value (49.9, null, false, 106, 4, NULL,null);
insert into Results value (35.99, null, false, 107, 5, NULL,null);
insert into Results value (59.8, null, false, 108, 1, NULL,null);

insert into Results value (56.78, null, false, 109, 9, NULL,null);
insert into Results value (100, null, false, 110, 10, NULL,null);
insert into Results value (102, null, false, 111, 6, NULL,null);

insert into Results value (105, null, false, 112, 2, NULL,null);
insert into Results value (106, null, false, 113, 3, NULL,null);
insert into Results value (9000, null, false, 114, 4, NULL,null);

insert into Results value (14.93, null, false, 115, 7, NULL,null);
insert into Results value (144, null, false, 116, 8, NULL,null);
insert into Results value (146, null, false, 117, 9, NULL,null);

insert into Results value (49.9,null , false, 118, 10, NULL,null);
insert into Results value (49.7, null, false, 119, 6, NULL,null);
insert into Results value (10, null, false, 120, 7, NULL,null);

insert into Results value (9.88, null, false, 121, 4, NULL,null);
insert into Results value (49.9, null, false, 122, 1, NULL,null);
insert into Results value (35.99, null, false, 123, 2, NULL,null);

insert into Results value (59.8, null, false, 124, 7, NULL,null);
insert into Results value (56.78, null, false, 125, 8, NULL,null);
insert into Results value (100, null, false, 126, 9, NULL,null);

insert into Results value (102, null, false, 127, 1, NULL,null);
insert into Results value (105, null, false, 128, 2, NULL,null);
insert into Results value (106, null, false, 129, 3, NULL,null);

insert into Results value (100, null, false, 126, 5, NULL,null);
insert into Results value (100, null, false, 126, 6, NULL,null);
insert into Results value (100, null, false, 126, 7, NULL,null);

select * from Results where compID = 126;
