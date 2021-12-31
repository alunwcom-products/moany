--
-- Database v6
-- 

-- remove older hibernate foreign key (pre-JPA)
ALTER TABLE moany.categories DROP KEY parent_id_key;
ALTER TABLE moany.transactions DROP KEY account_key;
ALTER TABLE moany.transactions DROP KEY category_key;

UPDATE moany.system_info SET value = '6' WHERE name = 'db_version';
COMMIT;
