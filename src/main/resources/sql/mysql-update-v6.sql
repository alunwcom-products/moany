--
-- Database v6
-- 

-- remove older hibernate foreign key (pre-JPA)
ALTER TABLE moany.categories DROP FOREIGN KEY parent_id_key;
ALTER TABLE moany.transactions DROP FOREIGN KEY account_key;
ALTER TABLE moany.transactions DROP FOREIGN KEY category_key;

UPDATE moany.system_info SET value = '6' WHERE name = 'db_version';
COMMIT;
