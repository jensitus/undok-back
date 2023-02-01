create or replace view fully as
    select p.first_name as firstname, p.last_name as lastname, c.keyword as keyword, c.id as c_id, p.client_id as p_client_id, c.id as id
    from persons p, clients c where c.id = p.client_id;