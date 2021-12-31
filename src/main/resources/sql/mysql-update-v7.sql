--
-- Database v7 update
-- 

-- drop category budgets
DROP TABLE IF EXISTS moany.category_budgets;

-- add category budgets
CREATE TABLE moany.category_budgets (
  uuid varchar(255) NOT NULL,
  category_id varchar(255) DEFAULT NULL,
  monthly_budget decimal(20,2) NOT NULL,
  start_date date NOT NULL,
  end_date date DEFAULT NULL,
  PRIMARY KEY ( uuid ),
  KEY category_key ( category_id ),
  KEY start_date_key ( start_date )
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- create stored procedure to migrate existing budgets
DROP PROCEDURE IF EXISTS temp_init_budgets;

DELIMITER $$
CREATE PROCEDURE temp_init_budgets()
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE title VARCHAR(255);
  DECLARE cat_key VARCHAR(255);
  DECLARE amount DECIMAL(20,2);
  DECLARE cursor1 CURSOR FOR SELECT uuid, name, monthly_budget FROM categories; 
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  OPEN cursor1;
  my_loop: LOOP
    IF done THEN
      LEAVE my_loop;
    END IF;
    FETCH cursor1 INTO cat_key, title, amount;
    IF amount IS NOT null THEN
      INSERT INTO category_budgets (uuid, category_id, monthly_budget, start_date) VALUES (UUID(), cat_key, amount, '1970-01-01');
    END IF;
  END LOOP;
  CLOSE cursor1;
END $$

DELIMITER ; -- space before semi-colon is needed!

-- run procedure
CALL temp_init_budgets;

-- drop procedure
DROP PROCEDURE temp_init_budgets;

-- remove old monthly_budget
ALTER TABLE moany.categories DROP COLUMN monthly_budget;

UPDATE moany.system_info SET value = '7' WHERE name = 'db_version';
COMMIT;
