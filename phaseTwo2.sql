CREATE TABLE users (
   email_address VARCHAR(255) PRIMARY KEY,
   name VARCHAR(255)
);
 
CREATE TABLE prospective_renter (
   email_address VARCHAR(255) PRIMARY KEY,
  desired_movein_date DATE,
   preferred_location VARCHAR(255),
   budget DECIMAL(10, 2),
   FOREIGN KEY (email_address) REFERENCES users(email_address)
);
 
CREATE TABLE agent (
   email_address VARCHAR(255) PRIMARY KEY,
   job_title VARCHAR(100),
   real_estate_agency VARCHAR(255),
   contact_info VARCHAR(255),
   FOREIGN KEY (email_address) REFERENCES users(email_address)
);
 
CREATE TABLE Address (
   address_id SERIAL PRIMARY KEY,
   address_line1 VARCHAR(255),
   address_line2 VARCHAR(255),
   city VARCHAR(100),
   state VARCHAR(100),
   zipcode VARCHAR(20)
);
 
CREATE TABLE credit_card (
   card_number VARCHAR(20) PRIMARY KEY,
   expiration_date DATE,
   CVV CHAR(3)
);
 
CREATE TABLE users_cc (
   card_number VARCHAR(20) PRIMARY KEY,
   email_address VARCHAR(255),
   FOREIGN KEY (email_address) REFERENCES users(email_address),
   FOREIGN KEY (card_number) REFERENCES credit_card(card_number)
);
 
CREATE TABLE credit_card_address (
   card_number VARCHAR(20) PRIMARY KEY,
   address_id INTEGER NOT NULL,
   FOREIGN KEY (card_number) REFERENCES credit_card(card_number),
   FOREIGN KEY (address_id) REFERENCES Address(address_id)
);
 
CREATE TABLE renter_address (
   address_id INTEGER PRIMARY KEY,
   email_address VARCHAR(255),
   FOREIGN KEY (email_address) REFERENCES prospective_renter(email_address),
   FOREIGN KEY (address_id) REFERENCES Address(address_id)
);
 
CREATE TABLE agent_address (
   address_id INTEGER PRIMARY KEY,
   email_address VARCHAR(255),
   FOREIGN KEY (email_address) REFERENCES agent(email_address),
   FOREIGN KEY (address_id) REFERENCES Address(address_id)
);

-- Damian's sql script

CREATE TABLE property (
   property_id    SERIAL       PRIMARY KEY,
   type            VARCHAR(50)  NOT NULL,
   square_footage  DECIMAL(10,2),
   address         VARCHAR(255),
   city            VARCHAR(100),
   state           VARCHAR(100),
   availability    BOOLEAN      DEFAULT TRUE,
   price           DECIMAL(10,2),
   description     TEXT,
   for_sale        BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE booking (
   booking_id     SERIAL       PRIMARY KEY,
   renter_email   VARCHAR(255) NOT NULL,
   card_number    VARCHAR(20)  NOT NULL,
   property_id    INTEGER      NOT NULL,
   start_date     DATE         NOT NULL,
   end_date       DATE         NOT NULL,
   FOREIGN KEY (renter_email) REFERENCES prospective_renter(email_address),
   FOREIGN KEY (card_number)  REFERENCES credit_card(card_number),
   FOREIGN KEY (property_id)  REFERENCES property(property_id)
);

CREATE TABLE manages (
   email_address  VARCHAR(255) NOT NULL,
   property_id    INTEGER      NOT NULL,
   PRIMARY KEY (email_address, property_id),
   FOREIGN KEY (email_address) REFERENCES agent(email_address),
   FOREIGN KEY (property_id)  REFERENCES property(property_id)
);

-- Subtype tables for property
CREATE TABLE commercial_buildings (
   property_id      INTEGER      PRIMARY KEY,
   type_of_business VARCHAR(255),
   FOREIGN KEY (property_id) REFERENCES property(property_id)
);

CREATE TABLE Apartments (
   property_id    INTEGER      PRIMARY KEY,
   number_of_rooms INT,
   building_type   VARCHAR(100),
   FOREIGN KEY (property_id) REFERENCES property(property_id)
);

CREATE TABLE Houses (
   property_id    INTEGER      PRIMARY KEY,
   number_of_rooms INT,
   FOREIGN KEY (property_id) REFERENCES property(property_id)
);

-- indexes
CREATE INDEX idx_property_type    ON property(type);
CREATE INDEX idx_booking_renter   ON booking(renter_email);
CREATE INDEX idx_booking_property ON booking(property_id);

 
CREATE INDEX idx_user_email ON users(email_address);
CREATE INDEX idx_card_number ON credit_card(card_number);
CREATE INDEX idx_address_id ON Address(address_id);