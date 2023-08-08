ALTER TABLE `DBSWITCH_ASSIGNMENT_CONFIG`
ADD COLUMN `table_name_case`  varchar(32) NOT NULL DEFAULT 'NONE' COMMENT '表名大小写转换策略' AFTER `target_schema`,
ADD COLUMN `column_name_case`  varchar(32) NOT NULL DEFAULT 'NONE' COMMENT '列名大小写转换策略' AFTER `table_name_case`;
