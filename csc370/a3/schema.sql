CREATE EXTENSION pgcrypto;

CREATE TABLE Users
(
	user_id 		SERIAL	 		NOT NULL -- Do not specify when inserting
		PRIMARY KEY,
	first_name		VARCHAR(20),
	last_name		VARCHAR(20),
	username		VARCHAR(20)		UNIQUE NOT NULL,
	password		VARCHAR(72)		NOT NULL,
	email			VARCHAR(320)	NOT NULL,
	skype_address	VARCHAR(50),
	created			TIMESTAMP		NOT NULL -- Do not specify when inserting
		DEFAULT NOW(),
	UNIQUE(username)
);

CREATE TABLE Languages -- Insert manually
(
	user_id		INTEGER	NOT NULL	REFERENCES Users (user_id),
	language	CHAR(3)	UNIQUE NOT NULL -- Three character code according to ISO 631-1
);

CREATE TABLE GradeLevel -- Insert manually
(
	grade_level_id	SERIAL	NOT NULL -- Do not specify when inserting
		PRIMARY KEY,
	name			TEXT	UNIQUE NOT NULL
);

INSERT INTO GradeLevel (name) VALUES ('k'), ('1'), ('2'), ('3'), ('4'), ('5'),
('6'), ('7'), ('8'), ('9'), ('10'), ('11'), ('12'), ('u');

CREATE TABLE Students
(
	student_id		INTEGER	NOT NULL	REFERENCES Users (user_id)
		PRIMARY KEY,
	grade_level_id	INTEGER	NOT NULL	REFERENCES GradeLevel (grade_level_id)
);

CREATE TABLE Teachers
(
	teacher_id		INTEGER	NOT NULL	REFERENCES Users (user_id)
		PRIMARY KEY
);

/*
 * Teachers may be available for help via Skype
 * for multiple intervals on any given day, so
 * we use this table to keep track.
 */
CREATE TABLE TeacherAvailability
(
	teacher_id	INTEGER		NOT NULL	REFERENCES Teachers (teacher_id),
	dow			INTEGER		NOT NULL	CHECK (dow >=0 and dow <=6), -- Day of week
	start_time	TIME		NOT NULL,
	end_time	TIME		NOT NULL
		CHECK (end_time-start_time >= TIME '0:00:00')
);

CREATE TABLE Category -- Insert manually (maybe?)
(
	category_id	SERIAL		NOT NULL -- Do not specify when inserting
		PRIMARY KEY,
	name		TEXT		UNIQUE NOT NULL,
	created		TIMESTAMP	NOT NULL -- Do not specify when inserting
		DEFAULT NOW()
);

INSERT INTO Category (name) VALUES ('math'), ('computer science');

CREATE TABLE Worksheets
(
	worksheet_id	SERIAL		NOT NULL -- Do not specify when inserting
		PRIMARY KEY,
	creator_id		INTEGER		NOT NULL	REFERENCES Users (user_id),
	grade_level_id	INTEGER		NOT NULL
		REFERENCES GradeLevel (grade_level_id),
	category_id		INTEGER		NOT NULL	REFERENCES Category (category_id),
	content			TEXT		NOT NULL, -- Link to CDN
	created			TIMESTAMP	NOT NULL -- Do not specify when inserting
		DEFAULT NOW()
);

CREATE TABLE Submitted
(
	submission_id	SERIAL	NOT NULl -- Do not specify when inserting
		PRIMARY KEY,
	worksheet_id	INTEGER	NOT NULL	REFERENCES Worksheets (worksheet_id),
	student_id		INTEGER NOT NULL	REFERENCES Students (student_id),
	content			TEXT	NOT NULL -- Link to CDN
);

CREATE TABLE SubmissionModifyDate
(
	submission_id	INTEGER	NOT NULL	REFERENCES Submitted (submission_id),
	modified		TIMESTAMP	NOT NULL -- Do not specify when inserting
		DEFAULT NOW()
);

CREATE TABLE Returned
(
	submission_id	INTEGER	NOT NULL	REFERENCES Submitted (submission_id),
	teacher_id		INTEGER	NOT NULL	REFERENCES Teachers (teacher_id),
	feedback		TEXT	NOT NULL, -- Link to CDN
	grade			INTEGER	NOT NULL	CHECK (grade >= 0 and grade <= 100)
);

CREATE FUNCTION create_user(
	first_name		VARCHAR(20),
	last_name		VARCHAR(20),
	_username		VARCHAR(20),
	password		VARCHAR(72),
	email			VARCHAR(320),
	skype_address	VARCHAR(50),
	is_teacher		BOOLEAN,
	grade_level		TEXT
)
RETURNS VOID
LANGUAGE plpgsql
as $$
BEGIN
	INSERT INTO Users (first_name, last_name, username,
		password, email, skype_address)
	VALUES (first_name, last_name, _username,
		crypt(password, gen_salt('bf')), email, skype_address);
	IF is_teacher THEN
		INSERT INTO Teachers (teacher_id)
		SELECT user_id FROM Users WHERE Users.username=_username;
	ELSE
		INSERT INTO Students (student_id, grade_level_id)
		SELECT * FROM
			(SELECT user_id FROM Users WHERE Users.username=_username) A
			NATURAL JOIN
			(SELECT grade_level_id FROM GradeLevel WHERE
				GradeLevel.name=grade_level) B;
	END IF;
END;
$$;

SELECT create_user('Nat', 'Dring', 'ndring', '123456', 'nd@uvic.ca', NULL, False, 'u');
SELECT create_user('Braydon', 'Horcoff', 'bhorcoff', '123456', 'bh@uvic.ca', NULL, False, 'u');
SELECT create_user('Parm', 'Johal', 'pjohal', '123456', 'pj@uvic.ca', NULL, False, 'u');
SELECT create_user('Paige', 'Loffler', 'ploffler', '123456', 'pl@uvic.ca', NULL, False, 'u');
SELECT create_user('Oliver', 'Tonnesen', 'otonnesen', '123456', 'ot@uvic.ca', NULL, False, 'u');

CREATE FUNCTION create_worksheet(
	creator_id		INTEGER,
	grade_level		TEXT,
	_category		TEXT,
	content			TEXT
)
RETURNS VOID
LANGUAGE plpgsql
as $$
BEGIN
	INSERT INTO Worksheets (creator_id, grade_level_id, category_id, content)
		SELECT creator_id, grade_level_id, category_id, content FROM
			GradeLevel JOIN Category ON True WHERE
				GradeLevel.name=grade_level and Category.name=_category;

END;
$$;
