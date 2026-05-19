-- 允许同一手机号注册多个身份（role 不同）：数据库需保证 (phone, role) 唯一，而不是仅 phone 唯一。
-- 执行前请备份；根据库中实际索引名修改 DROP 语句。

-- 查看 phone 相关索引：
-- SHOW INDEX FROM user_account;

-- 若存在「仅 phone」的唯一索引，先删除（将 <索引名> 换成上一步查到的 Key_name）：
-- ALTER TABLE user_account DROP INDEX `<索引名>`;

-- 增加 (phone, role) 联合唯一（若已存在则不要重复执行）：
-- ALTER TABLE user_account ADD UNIQUE KEY uk_user_account_phone_role (phone, role);
