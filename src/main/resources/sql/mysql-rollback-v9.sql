--
-- Database v8 rollback
--

-- add sort code and active properties to accounts
ALTER TABLE `accounts` DROP COLUMN `sortcode`;
ALTER TABLE `accounts` DROP COLUMN `active`;

UPDATE system_info SET value = '8' WHERE name = 'db_version';
COMMIT;
