UPDATE account_master SET fk_group_id = 'G0000001';

ALTER TABLE sale_bill_detail ADD COLUMN slab integer default 0;

ALTER TABLE oldb2_2 ADD COLUMN amount_type INT(11); 
ALTER TABLE account_master ADD COLUMN lock_date DATE;