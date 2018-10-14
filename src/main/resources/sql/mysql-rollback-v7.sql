--
-- Database v7 rollback TODO
-- 

-- remove old monthly_budget
-- ALTER TABLE moany.categories DROP COLUMN monthly_budget;

-- add category budgets
-- CREATE TABLE moany.category_budgets (
--   uuid varchar(255) NOT NULL,
--   category_id varchar(255) DEFAULT NULL,
--   monthly_budget decimal(20,2) NOT NULL,
--   start_date date NOT NULL,
--   end_date date DEFAULT NULL,
--   PRIMARY KEY ( uuid ),
--   KEY category_key ( category_id ),
--   KEY start_date_key ( start_date )
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- UPDATE moany.system_info SET value = '7' WHERE name = 'db_version';
-- COMMIT;

