-- 设备使用占用手动释放：管理员可将进行中的使用标记为已释放，用户可再次下单。
-- 执行一次即可。
ALTER TABLE usage_record
    ADD COLUMN released_at DATETIME NULL COMMENT '管理员手动释放时间，非空则不再占用设备' AFTER created_at;
