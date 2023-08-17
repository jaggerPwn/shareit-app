CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL UNIQUE,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  description varchar(10000) NOT null,
  user_id BIGINT NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_request PRIMARY KEY (id),
  CONSTRAINT fk_user_4 FOREIGN KEY (user_id) REFERENCES users(id)
 );
CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(2056) NOT NULL,
  available BOOLEAN NOT NULL,
  user_id BIGINT NOT NULL,
  request_id BIGINT,
  CONSTRAINT pk_user_2 PRIMARY KEY (id),
  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  item_id BIGINT NOT NULL,
  booker_id BIGINT NOT NULL,
  status varchar(12) NOT NULL,
  CONSTRAINT pk_booking_3 PRIMARY KEY (id),
  CONSTRAINT fk_user_2 FOREIGN KEY (booker_id) REFERENCES users(id),
  CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  text varchar(10000) NOT null,
  item_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT pk_comment PRIMARY KEY (id),
  CONSTRAINT fk_user_3 FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_item_2 FOREIGN KEY (item_id) REFERENCES items(id)
 );


