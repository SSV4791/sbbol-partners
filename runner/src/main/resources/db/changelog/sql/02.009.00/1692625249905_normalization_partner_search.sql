-- liquibase formatted sql
-- changeset 17866673:1692625249905_normalization_partner_search

update partner as p
set search = concat_ws(
    ' ',
    NULLIF(p.inn, ''),
    NULLIF(p.kpp, ''),
    NULLIF(p.org_name, ''),
    NULLIF(p.second_name, ''),
    NULLIF(p.first_name, ''),
    NULLIF(p.middle_name, '')
    )
where p.uuid in (
    select p10.uuid
    from (
             select *
             from partner p
             where p.uuid in (
                 select p2.uuid
                 from (
                          select p1.uuid,
                                 concat_ws(
                                     ' ',
                                     NULLIF(p1.inn, ''),
                                     NULLIF(p1.kpp, ''),
                                     NULLIF(p1.org_name, ''),
                                     NULLIF(p1.second_name, ''),
                                     NULLIF(p1.first_name, ''),
                                     NULLIF(p1.middle_name, '')
                                     )     as expectedSearch,
                                 p1.search as actualSearch
                          from partner p1
                          where p1.type = 'PARTNER'
                      ) p2
                 where p2.actualSearch <> p2.expectedSearch
             )
         ) p10
    where concat_ws(
              ' ',
              NULLIF(p10.digital_id, ''),
              NULLIF(p10.inn, ''),
              NULLIF(p10.kpp, ''),
              NULLIF(p10.org_name, ''),
              NULLIF(p10.second_name, ''),
              NULLIF(p10.first_name, ''),
              NULLIF(p10.middle_name, '')
              ) not in (
              select concat_ws(
                         ' ',
                         NULLIF(p3.digital_id, ''),
                         NULLIF(p3.inn, ''),
                         NULLIF(p3.kpp, ''),
                         NULLIF(p3.org_name, ''),
                         NULLIF(p3.second_name, ''),
                         NULLIF(p3.first_name, ''),
                         NULLIF(p3.middle_name, '')
                         ) as duplicateKey
              from partner p3
              where p3.type = 'PARTNER'
                and p3.digital_id = p10.digital_id
              group by duplicateKey
              having count(*) > 1
          )
)
