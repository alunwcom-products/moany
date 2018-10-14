--
-- Database v5
-- 

-- remove account start date column
ALTER TABLE moany.accounts DROP COLUMN start_date;

-- add budget
CREATE TABLE moany.budgetitems (
  uuid varchar(255) NOT NULL,
  amount decimal(19,2) NOT NULL,
  day_of_period int(11) NOT NULL,
  description varchar(255) DEFAULT NULL,
  end_date date DEFAULT NULL,
  last_modified_date datetime NOT NULL,
  period_type varchar(255) NOT NULL,
  start_date date NOT NULL,
  account varchar(255) NOT NULL,
  category varchar(255) DEFAULT NULL,
  PRIMARY KEY ( uuid ),
  KEY account_key ( account ),
  KEY category_key ( category )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

UPDATE moany.system_info SET value = '5' WHERE name = 'db_version';
COMMIT;

