-- 1) Users
INSERT INTO users (email_address, name) VALUES
  ('renter1@example.com', 'Alice Johnson'),
  ('renter2@example.com', 'Bob Smith'),
  ('renter3@example.com', 'Evelyn Davis'),
  ('renter4@example.com', 'Franklin Lee'),
  ('agent1@agency.com',  'Carol Williams'),
  ('agent2@homes.com',   'David Brown'),
  ('agent3@realty.net',  'Grace Kim'),
  ('agent4@estate.org',  'Henry Zhang');

-- 2) Prospective renters
INSERT INTO prospective_renter (email_address, desired_movein_date, preferred_location, budget) VALUES
  ('renter1@example.com', '2025-06-01', 'Chicago, IL',   2000.00),
  ('renter2@example.com', '2025-07-15', 'Naperville, IL', 2500.00),
  ('renter3@example.com', '2025-08-01', 'Oak Park, IL',  1800.00),
  ('renter4@example.com', '2025-09-10', 'Chicago, IL',   2200.00);

-- 3) Agents
INSERT INTO agent (email_address, job_title, real_estate_agency, contact_info) VALUES
  ('agent1@agency.com', 'Senior Agent',    'Premier Realty', '555-1234'),
  ('agent2@homes.com',  'Junior Agent',    'Dream Homes',    '555-5678'),
  ('agent3@realty.net', 'Associate Agent', 'Urban Realty',   '555-8765'),
  ('agent4@estate.org','Lead Agent',       'Elite Estates',  '555-4321');

-- 4) Addresses
INSERT INTO Address (address_line1, address_line2, city, state, zipcode) VALUES
  ('123 Main St',       '',         'Chicago',     'IL', '60616'),
  ('456 Oak Ave',       'Apt 5B',   'Evanston',    'IL', '60201'),
  ('789 Pine Rd',       '',         'Naperville',  'IL', '60540'),
  ('321 Market St',     'Suite 100','Chicago',     'IL', '60654'),
  ('654 Elm St',        '',         'Aurora',      'IL', '60502'),
  ('987 Maple Dr',      'Unit 12',  'Springfield', 'IL', '62701'),
  ('111 River Rd',      '',         'Oak Park',    'IL', '60302'),
  ('222 Lakeview Blvd', 'Apt 10',   'Chicago',     'IL', '60611'),
  ('333 Market St',     'Suite 200','Evanston',    'IL', '60202'),
  ('444 Sunset Ave',    '',         'Aurora',      'IL', '60503');

-- 5) Credit cards
INSERT INTO credit_card (card_number, expiration_date, cvv) VALUES
  ('4111111111111111', '2025-12-31', '123'),
  ('5500000000000004', '2026-06-30', '456'),
  ('6011000990139424', '2026-11-30', '789'),
  ('3530111333300000','2027-05-31', '012');

-- 6) Link cards to renters
INSERT INTO users_cc (card_number, email_address) VALUES
  ('4111111111111111','renter1@example.com'),
  ('5500000000000004','renter2@example.com'),
  ('6011000990139424','renter3@example.com'),
  ('3530111333300000','renter4@example.com');

-- 7) Billing addresses for cards
INSERT INTO credit_card_address (card_number, address_id) VALUES
  ('4111111111111111', 1),
  ('5500000000000004', 2),
  ('6011000990139424', 3),
  ('3530111333300000', 4);

-- 8) Residential addresses for renters
INSERT INTO renter_address (address_id, email_address) VALUES
  (1, 'renter1@example.com'),
  (2, 'renter2@example.com'),
  (9, 'renter3@example.com'),
  (10,'renter4@example.com');

-- 9) Office addresses for agents
INSERT INTO agent_address (address_id, email_address) VALUES
  (3, 'agent1@agency.com'),
  (4, 'agent2@homes.com'),
  (7, 'agent3@realty.net'),
  (8, 'agent4@estate.org');

-- 10) Properties
INSERT INTO property (type, square_footage, address, city, state, availability, price, description) VALUES
  ('House',      2000.00, '123 Elm St'              , 'Naperville', 'IL', TRUE , 350000.00, 'Cozy 3-bed house.'),
  ('Apartment',   850.00, '456 Oak St Apt 2A'       , 'Chicago'   , 'IL', TRUE ,   1500.00, '1-bed downtown apt.'),
  ('Commercial', 5000.00, '789 Business Rd'         , 'Evanston'  , 'IL', FALSE,   5000.00, 'Office space.'),
  ('Apartment',   900.00, '321 Pine Tower Apt 3C'   , 'Chicago'   , 'IL', TRUE ,   1800.00, '2-bed lakeview.'),
  ('House',      1500.00, '654 Elm St'              , 'Springfield','IL',FALSE, 250000.00, '2-bed suburban.'),
  ('House',      1800.00, '111 River Rd'            , 'Oak Park'  , 'IL', TRUE , 320000.00, '4-bed Oak Park.'),
  ('Apartment',   700.00, '222 Lakeview Blvd Apt 10', 'Chicago'   , 'IL', TRUE ,   1600.00, 'Cozy studio.');

-- 11) Bookings
INSERT INTO booking (renter_email, card_number, property_id, start_date, end_date) VALUES
  ('renter1@example.com','4111111111111111',2,'2025-06-01','2026-05-31'),
  ('renter2@example.com','5500000000000004',1,'2025-07-15','2026-07-14'),
  ('renter3@example.com','6011000990139424',6,'2025-08-01','2026-07-31'),
  ('renter4@example.com','3530111333300000',7,'2025-09-10','2026-09-09');

-- 12) Agent–Property assignments
INSERT INTO manages (email_address, property_id) VALUES
  ('agent1@agency.com',1),
  ('agent1@agency.com',3),
  ('agent2@homes.com',2),
  ('agent2@homes.com',4),
  ('agent2@homes.com',5),
  ('agent3@realty.net',6),
  ('agent4@estate.org',7);

-- 13) Subclasses
INSERT INTO commercial_buildings (property_id, type_of_business) VALUES
  (3, 'Office');
INSERT INTO Apartments (property_id, number_of_rooms, building_type) VALUES
  (2, 1, 'High-rise'),
  (4, 2, 'Mid-rise'),
  (7, 1, 'Loft');
INSERT INTO Houses (property_id, number_of_rooms) VALUES
  (1, 3),
  (5, 2),
  (6, 4);
