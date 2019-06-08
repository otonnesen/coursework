-- a4_ferry_queries.sql
-- CSC 106 - Fall 2017
--
-- File for Assignment 4 (Question 2) queries.
--
-- 11/24/2017
-- Oliver Tonnesen
-- V00885732


.read a4_ferry_data.sql

.mode column
.header on

-------------------------------------------
-- Put your answers for Question 2 below --
-------------------------------------------


-- Question 2 a --
.print 'Question 2a'
.width 35 15
select vessel_name, count(*) as sailing_count from fleet natural join sailings group by vessel_name order by sailing_count desc;


.print ''
-- Question 2 b --
.print 'Question 2b'
.width 15 15
select route_number, count(*) as sailing_count from routes natural join sailings where month = 10 group by route_number order by route_number;


.print ''
-- Question 2 c --
.print 'Question 2c'
.width 10 20 20
select day, sum(human_capacity) as total_human_capacity, sum(car_capacity) as total_car_capacity from routes natural join fleet natural join sailings where route_number = 1 and month = 9 group by day order by day;


.print ''
-- Question 2 d --
.print 'Question 2d'
.width 15 35 15
select route_number, vessel_name, year_built from sailings natural join fleet natural join (select route_number, min(year_built) as year_built from routes natural join fleet natural join sailings group by route_number) group by vessel_name, route_number order by route_number;


.print ''
-- Question 2 e --
.print 'Question 2e'
.width 35 15
select * from (select vessel_name, count(*) as num_routes from (select route_number, vessel_name from routes natural join sailings natural join fleet group by route_number, vessel_name) group by vessel_name) where num_routes > 1 order by vessel_name;


.print ''
-- Question 2 f --
.print 'Question 2f'
.width 35 10 15
select vessel_name, year_built, count(*) as late_count from (select vessel_name, year_built from sailings natural join fleet natural join routes where duration > nominal_duration) group by vessel_name order by year_built, vessel_name;

