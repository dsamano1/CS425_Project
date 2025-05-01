
# 🏠 Real Estate Management CLI Application

A Java-based command-line application backed by PostgreSQL that supports user registration, property management, and secure handling of payment and address information for agents and prospective renters.

---

## 📋 Features

### 👤 User Registration
- Users can register as:
  - **Agents**: Provide job title, agency, and contact information
  - **Prospective Renters**: Provide move-in date, preferred location, and budget
- Users are uniquely identified by their **email address**

### 📍 Address Management (Renters Only)
- Renters can:
  - Add new addresses
  - Delete addresses (only if not used as billing addresses)
  - Update existing address information

### 💳 Credit Card Management (Renters Only)
- Add multiple credit cards
- Associate each card with one of the renter’s saved addresses
- Modify or delete cards

### 🏢 Property Management (Agents Only)
- Agents can:
  - Add new property listings (type, square footage, address, price, description)
  - Delete property listings
  - Modify property details

### 🛡️ Role-Based Access Control
- Renters cannot access property management options
- Agents cannot manage credit cards or renter-specific data
- Role is validated by querying the `agent` or `prospective_renter` tables during interactions

---

## 🛠 Technologies

- **Java (JDK 17+)**
- **PostgreSQL** as the relational database
- **JDBC** for database connectivity
- **Command-Line Interface** (pure terminal input/output)
- Modular service classes:
  - `UserService.java`
  - `PaymentService.java`
  - `PropertyService.java`

---

## 🗂 Data Model Highlights

- `users` — base user table (email, name)
- `agent` — agent-specific information
- `prospective_renter` — renter preferences (move-in date, location, budget)
- `address` — shared across agents/renters/credit cards
- `renter_address` — maps renters to addresses
- `credit_card`, `user_cc`, `credit_card_address` — normalized payment structure
- `property` and `manages` — property listings and their agent associations

---

## ✅ Getting Started

1. Compile:
   ```bash
   javac -cp ".:postgresql-42.7.3.jar" *.java
   ```

2. Run:
   ```bash
   java -cp ".:postgresql-42.7.3.jar" Main
   ```

3. Make sure PostgreSQL is running and your database (`mydb`) has the schema loaded.

---

## 🚀 Use Cases

- A renter signs up, adds multiple addresses, and links their credit cards to billing addresses
- An agent signs up, lists new properties for rent, and updates pricing
- Role enforcement prevents misuse (e.g. renters modifying properties)
