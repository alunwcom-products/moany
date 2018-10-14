--
-- Database v4
-- 

-- user/authority tables
CREATE TABLE moany.users (
  id varchar(255) NOT NULL,
  enabled bit(1) NOT NULL,
  password varchar(255) DEFAULT NULL,
  username varchar(255) NOT NULL,
  PRIMARY KEY ( id ),
  UNIQUE KEY username_key (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE moany.authorities (
  id varchar(255) NOT NULL,
  authority varchar(255) DEFAULT NULL,
  user varchar(255) NOT NULL,
  PRIMARY KEY ( id ),
  KEY user_key ( user )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

UPDATE moany.system_info SET value = '4' WHERE name = 'db_version';
COMMIT;

