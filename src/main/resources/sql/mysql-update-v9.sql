--
-- Database v9 update
-- 

-- add sort code and active properties to accounts
ALTER TABLE `accounts` ADD COLUMN `sortcode` varchar(255);
ALTER TABLE `accounts` ADD COLUMN `active` bit(1) NOT NULL DEFAULT 1;

-- account_summary (v2)
create or replace view account_summary as
with min_date as (
	select
		account,
        min(trans_date) as trans_date
	from transactions
    group by account
),
max_date as (
	select
		account,
        max(trans_date) as trans_date
	from transactions
    group by account
),
latest_balance as (
	select
		account,
        sum(net_amount) as net_total
	from transactions
    group by account
)
select
    a.uuid,
    a.name,
    a.account_num,
    a.sortcode,
    a.type,
    a.active,
    ifnull(min_date.trans_date, date('1970-01-01')) as `earliest`,
    ifnull(max_date.trans_date, date('1970-01-01')) as `latest`,
    a.starting_balance,
    ifnull(latest_balance.net_total, 0) + a.starting_balance `latest_balance`
from accounts a
         left outer join min_date on min_date.account = a.uuid
         left outer join max_date on max_date.account = a.uuid
         left outer join latest_balance on latest_balance.account = a.uuid
order by a.name;

UPDATE system_info SET value = '9' WHERE name = 'db_version';
COMMIT;

