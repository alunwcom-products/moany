--
-- Database v1
--

CREATE TABLE moany.accounts (
  uuid varchar(255) NOT NULL,
  account_num varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  type varchar(255) NOT NULL,
  starting_balance decimal(20,2) DEFAULT NULL,
  start_date date DEFAULT NULL,
  PRIMARY KEY ( uuid ),
  UNIQUE KEY account_num_key ( account_num )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE moany.categories (
  uuid varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  parent_id varchar(255) DEFAULT NULL,
  PRIMARY KEY ( uuid ),
  KEY parent_id_key ( parent_id )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE moany.transactions (
  uuid varchar(255) NOT NULL,
  amount decimal(20,2) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  comment varchar(255) DEFAULT NULL,
  entry_date datetime DEFAULT NULL,
  source_name varchar(255) DEFAULT NULL,
  source_row bigint(20) DEFAULT NULL,
  source_type varchar(255) DEFAULT NULL,
  statement_balance decimal(20,2) DEFAULT NULL,
  account_balance decimal(20,2) DEFAULT NULL,
  trans_date date DEFAULT NULL,
  type varchar(255) DEFAULT NULL,
  account varchar(255) NOT NULL,
  category varchar(255) DEFAULT NULL,
  PRIMARY KEY ( uuid ),
  KEY account_key ( account ),
  KEY category_key ( category )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE moany.system_info (
  name varchar(255) NOT NULL,
  value varchar(255),
  PRIMARY KEY ( name )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO moany.system_info (name, value) VALUES ('db_version','1');
COMMIT;
