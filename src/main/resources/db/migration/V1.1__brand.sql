CREATE SCHEMA IF NOT EXISTS ciq;

CREATE table `brand`(
    ${namedentity},
    ${audit},
    UNIQUE
    (`name`,`deleted_ind`)
);