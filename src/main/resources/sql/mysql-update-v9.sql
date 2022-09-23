--
-- Database v9 update
-- 

-- remove budgetitems table, this functional has been removed
DROP TABLE budgetitems;

UPDATE system_info SET value = '9' WHERE name = 'db_version';
COMMIT;
