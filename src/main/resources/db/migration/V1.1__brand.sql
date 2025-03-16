CREATE table `brand`(
    `pid` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `name` VARCHAR(50) NOT NULL,
    `enabled` TINYINT NOT NULL DEFAULT 1,
    `create_datetm` DATETIME NOT NULL DEFAULT now(),
    `last_update_datetm` DATETIME,
    `deleted_ind` BIGINT NOT NULL DEFAULT 0,
    `create_user` VARCHAR(50) NOT NULL,
    `last_update_user` VARCHAR(50),
    `delete_user` VARCHAR(50)
);

CREATE UNIQUE INDEX IDX_UNQ_BRAND_NAME_DEL ON brand(name, deleted_ind);
