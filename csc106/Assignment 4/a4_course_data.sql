-- a4_course_data.sql
-- CSC 106 - Fall 2017
--
-- Construct a database containing Fall 2017 course scheduling
-- data.
--
-- B. Bird - 11/05/2017

-- If the tables already exist, delete them --
drop table if exists course_names;
drop table if exists course_sections;
drop table if exists prerequisites;

-----------------------
-- Create the tables --
-----------------------
create table course_names( subject_code text, course_number int, course_name text);
create table course_sections( subject_code text, course_number int, section_name text, instructor_firstname text, instructor_lastname text);
create table prerequisites( subject_code text, course_number int, prereq_subject text, prereq_number int);

---------------------------------
-- Insert data into each table --
---------------------------------

-- Course names --
insert into course_names values ('CSC',100,'Elementary Computing');
insert into course_names values ('CSC',105,'Computers and Information Processing');
insert into course_names values ('CSC',106,'The Practice of Computer Science');
insert into course_names values ('CSC',110,'Fundamentals of Programming I');
insert into course_names values ('CSC',111,'Fundamentals of Programming with Engineering Applications');
insert into course_names values ('CSC',115,'Fundamentals of Programming II');
insert into course_names values ('CSC',116,'Fundamentals of Programming with Engineering Applications II');
insert into course_names values ('CSC',225,'Algorithms & Data Structures I');
insert into course_names values ('CSC',226,'Algorithms & Data Structures II');
insert into course_names values ('CSC',230,'Intro. to Computer Architecture');
insert into course_names values ('SENG',265,'Software Development Methods');
insert into course_names values ('CSC',330,'Programming Languages');
insert into course_names values ('CSC',370,'Database Systems');
insert into course_names values ('SENG',474,'Data Mining');

-- Sections --
insert into course_sections values ('CSC',100,'A01','Rich', 'Little');
insert into course_sections values ('CSC',100,'A02','Rich', 'Little');
insert into course_sections values ('CSC',105,'A01','Ahmad', 'Abdullah');
insert into course_sections values ('CSC',106,'A01','Bill', 'Bird');
insert into course_sections values ('CSC',106,'A02','Bill', 'Bird');
insert into course_sections values ('CSC',110,'A01','Jens', 'Weber');
insert into course_sections values ('CSC',110,'A02','Jens', 'Weber');
insert into course_sections values ('CSC',110,'A03','Tibor', 'van Rooij');
insert into course_sections values ('CSC',110,'A04','Tibor', 'van Rooij');
insert into course_sections values ('CSC',111,'A01','Hausi', 'Muller');
insert into course_sections values ('CSC',111,'A02','Hausi', 'Muller');
insert into course_sections values ('CSC',111,'A03','Hausi', 'Muller');
insert into course_sections values ('CSC',111,'A04','Hausi', 'Muller');
insert into course_sections values ('CSC',115,'A01','Tibor', 'van Rooij');
insert into course_sections values ('CSC',116,'A01','Daniel', 'German');
insert into course_sections values ('CSC',116,'A02','Bill', 'Bird');
insert into course_sections values ('CSC',225,'A01','Wendy', 'Myrvold');
insert into course_sections values ('CSC',226,'A01','Nishant', 'Mehta');
insert into course_sections values ('CSC',230,'A01','LillAnne', 'Jackson');
insert into course_sections values ('CSC',230,'A02','LillAnne', 'Jackson');
insert into course_sections values ('SENG',265,'A01','Daniela', 'Damian');
insert into course_sections values ('SENG',265,'A02','Daniela', 'Damian');
insert into course_sections values ('CSC',330,'A01','Mantis', 'Cheng');
insert into course_sections values ('CSC',370,'A01','Alex', 'Thomo');
insert into course_sections values ('SENG',474,'A01','George', 'Tzanetakis');


-- Prerequisites --

insert into prerequisites values ('CSC', 115, 'CSC', 110);
insert into prerequisites values ('CSC', 116, 'CSC', 111);
insert into prerequisites values ('CSC', 225, 'CSC', 115);
insert into prerequisites values ('CSC', 226, 'CSC', 225);
insert into prerequisites values ('CSC', 230, 'CSC', 115);
insert into prerequisites values ('SENG', 265, 'CSC', 115);
insert into prerequisites values ('CSC', 330, 'CSC', 226);
insert into prerequisites values ('CSC', 330, 'CSC', 230);
insert into prerequisites values ('CSC', 330, 'SENG', 265);
insert into prerequisites values ('CSC', 370, 'CSC', 226);
insert into prerequisites values ('CSC', 370, 'SENG', 265);
insert into prerequisites values ('SENG', 474, 'SENG', 265);



.mode column
.header on
