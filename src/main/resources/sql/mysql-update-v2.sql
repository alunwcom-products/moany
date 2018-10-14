--
-- Database v2
-- 

-- Add new net amount, after renaming previous amount
ALTER TABLE moany.transactions CHANGE COLUMN amount statement_amount decimal(20,2) DEFAULT NULL;
ALTER TABLE moany.transactions ADD COLUMN net_amount decimal(20,2) DEFAULT NULL;

UPDATE system_info SET value = '2' WHERE name = 'db_version';
COMMIT;
