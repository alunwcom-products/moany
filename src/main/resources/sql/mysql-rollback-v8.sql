--
-- Database v8 rollback
--

-- update 'user' column name to avoid h2 v2 keyword clash
ALTER TABLE moany.authorities CHANGE COLUMN user_id user varchar(255) NOT NULL;

UPDATE moany.system_info SET value = '7' WHERE name = 'db_version';
COMMIT;
