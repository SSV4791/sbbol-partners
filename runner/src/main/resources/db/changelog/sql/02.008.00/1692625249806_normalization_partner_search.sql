-- liquibase formatted sql
-- changeset 17866673:1692625249806_normalization_partner_search

update partner as p
set search = concat_ws(
    ' ',
    NULLIF(p.inn, ''),
    NULLIF(p.kpp, ''),
    NULLIF(p.org_name, ''),
    NULLIF(p.second_name, ''),
    NULLIF(p.first_name, ''),
    NULLIF(p.middle_name, '' )
    )
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
                        NULLIF(p1.middle_name, '' )
                        )  as expectedSearch,
                    p1.search           as actualSearch
             from partner p1
             where p1.type = 'PARTNER'
         ) p2
    where p2.actualSearch <> p2.expectedSearch
)
  and not exists(
    select *
    from partner p3
    where p3.search = concat_ws(
        ' ',
        NULLIF(p.inn, ''),
        NULLIF(p.kpp, ''),
        NULLIF(p.org_name, ''),
        NULLIF(p.second_name, ''),
        NULLIF(p.first_name, ''),
        NULLIF(p.middle_name, '')
        )
    )
