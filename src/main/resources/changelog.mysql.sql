-- liquibase formatted sql

-- changeset aluwilliam:1672928780208-1
CREATE TABLE accounts (uuid VARCHAR(255) NOT NULL, account_num VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, starting_balance DECIMAL(20, 2) NULL, start_date date NULL, CONSTRAINT PK_ACCOUNTS PRIMARY KEY (uuid), UNIQUE (account_num));

-- changeset aluwilliam:1672928780208-2
CREATE TABLE categories (uuid VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, parent_id VARCHAR(255) NULL, CONSTRAINT PK_CATEGORIES PRIMARY KEY (uuid));

-- changeset aluwilliam:1672928780208-3
CREATE TABLE system_info (name VARCHAR(255) NOT NULL, value VARCHAR(255) NULL, CONSTRAINT PK_SYSTEM_INFO PRIMARY KEY (name));

-- changeset aluwilliam:1672928780208-4
CREATE TABLE transactions (uuid VARCHAR(255) NOT NULL, amount DECIMAL(20, 2) NULL, `description` VARCHAR(255) NULL, comment VARCHAR(255) NULL, entry_date datetime NULL, source_name VARCHAR(255) NULL, source_row BIGINT NULL, source_type VARCHAR(255) NULL, statement_balance DECIMAL(20, 2) NULL, account_balance DECIMAL(20, 2) NULL, trans_date date NULL, type VARCHAR(255) NULL, account VARCHAR(255) NOT NULL, category VARCHAR(255) NULL, CONSTRAINT PK_TRANSACTIONS PRIMARY KEY (uuid));

-- changeset aluwilliam:1672928780208-5
CREATE INDEX account_key ON transactions(account);

-- changeset aluwilliam:1672928780208-6
CREATE INDEX category_key ON transactions(category);

-- changeset aluwilliam:1672928780208-7
CREATE INDEX parent_id_key ON categories(parent_id);

