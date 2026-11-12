DROP TABLE IF EXISTS course;
create table course (
 id int not null AUTO_INCREMENT PRIMARY KEY,
 code varchar(100) not null,
 name varchar(100) not null,
 description varchar(100),
 credits int
);
