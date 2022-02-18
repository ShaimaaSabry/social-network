CREATE TABLE users(
	id INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	first_name VARCHAR(20) NOT NULL,
	last_name VARCHAR(20) NOT NULL,
	email VARCHAR(50) NOT NULL UNIQUE,
	email_verified BOOLEAN NOT NULL,
	password_hash VARCHAR(200),
	profilepicture_id INT
);

CREATE TABLE profilepictures(
	id INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	bucket_name VARCHAR(50) NOT NULL,
	blob_name VARCHAR(100) NOT NULL,
	valid_selfie BOOLEAN NOT NULL,
	user_id INT NOT NULL,
	CONSTRAINT fk_user
      FOREIGN KEY(user_id) 
	  REFERENCES users(id)
);

ALTER TABLE users
	ADD FOREIGN KEY (profilepicture_id) REFERENCES profilepictures(id);