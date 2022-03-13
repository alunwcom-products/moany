--
-- Database v8 update
-- 

-- update 'user' column name to avoid h2 v2 keyword clash
ALTER TABLE moany.authorities CHANGE COLUMN user user_id varchar(255) NOT NULL;

UPDATE moany.system_info SET value = '8' WHERE name = 'db_version';
COMMIT;
