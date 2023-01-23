--
-- Database v9 update
-- 

-- create view for daily in/out balance and running balance total
CREATE OR REPLACE VIEW daily_totals AS
WITH data AS (
  SELECT
    trans_date,
    if(net_amount > 0, net_amount, 0) AS credit,
    if(net_amount <= 0, net_amount, 0) AS debit
  FROM transactions
  -- where trans_date between '2022-01-01' and '2022-12-31'
  ORDER BY trans_date
)
SELECT
  trans_date,
  SUM(credit) AS incoming,
  SUM(debit) AS outgoing,
  SUM(credit) + SUM(debit) as net,
  SUM(SUM(credit) + SUM(debit)) OVER (ORDER BY trans_date) AS balance
FROM data
GROUP BY trans_date;

UPDATE system_info SET value = '9' WHERE name = 'db_version';
COMMIT;
