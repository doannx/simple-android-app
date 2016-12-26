CREATE TABLE tbl_function (id integer primary key autoincrement not null, description text);
CREATE TABLE tbl_brand (id integer primary key autoincrement not null, brand text, generic text, functionId int);

