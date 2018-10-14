--
-- Database v3
-- 

-- Add new monthly_budget column
ALTER TABLE `categories` ADD COLUMN `monthly_budget` decimal(20,2) DEFAULT NULL;

UPDATE `system_info` SET `value` = '3' WHERE `name` = 'db_version';
COMMIT;

