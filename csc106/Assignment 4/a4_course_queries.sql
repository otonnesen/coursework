-- a4_course_queries.sql
-- CSC 106 - Fall 2017
--
-- File for Assignment 4 (Question 1) queries.
--
-- 11/24/2017
-- Oliver Tonnesen
-- V00885732


.read a4_course_data.sql

.mode column
.header on

-------------------------------------------
-- Put your answers for Question 1 below --
-------------------------------------------


-- Question 1 a --
.print 'Question 1a'
.width 13 13 35
select * from course_names order by course_number;


.print ''
-- Question 1 b --
.print 'Question 1b'
.width 13 13 13 20 20
select * from course_sections where course_number > 199 and course_number < 300 order by course_number;


.print ''
-- Question 1 c --
.print 'Question 1c'
.width 15 15 30 15
select subject_code, course_number, course_name, section_name from course_sections natural join course_names where instructor_firstname = "Tibor" and instructor_lastname = "van Rooij" order by course_number;


.print ''
-- Question 1 d --
.print 'Question 1d'
.width 15 15 30 15
select instructor_firstname, instructor_lastname, course_name, section_name from course_sections natural join course_names where subject_code = "SENG" order by course_number;


.print ''
-- Question 1 e --
.print 'Question 1e'
.width 15 15 32
select subject_code, course_number, course_name from course_names natural join prerequisites where prereq_subject = "CSC" and prereq_number = "115" order by subject_code;


.print ''
-- Question 1 f --
.print 'Question 1f'
.width 14 14 36 14
select subject_code, course_number, course_name, count(*) as num_sections from course_names natural join course_sections group by subject_code, course_number order by course_number;


.print ''
-- Question 1 g --
.print 'Question 1g'
.width 20 20 15
select * from (select instructor_firstname, instructor_lastname, count(*) as num_sections from course_names natural join course_sections group by instructor_firstname, instructor_lastname) where num_sections > 1 order by num_sections desc, instructor_lastname asc;

