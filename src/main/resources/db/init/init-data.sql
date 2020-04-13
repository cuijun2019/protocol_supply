alter table users MODIFY enabled bit(10);

INSERT INTO users (
	ID,
	company,
	create_time,
	email,
	enabled,
	fullname,
	is_delete,
	PASSWORD,
	sex,
	telephone,
	update_time,
	username,
	attach_id
)
VALUES
	(
		1,
		'广东宜通世纪股份科技有限公司',
		now(),
		'5555@163.com',
		'1',
		'监控人员',
		2,
		'$2a$10$Lq4xOF33YvM6voT15RuTw.0kmfTWOlFX/v4wM1n2OaRQDbBOVCPxi',
		'男',
		'18319670614',
		now(),
		'admin',
		NULL
	);


INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (1, '2020-04-07 16:15:34', 'admin', 2, 'index', '首页', '/index', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (2, '2020-04-07 16:25:23', 'admin', 2, 'todo', '待办', '/index/todo', 1, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (3, '2020-04-07 17:02:13', 'admin', 2, 'done', '已办', '/index/done', 1, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (4, '2020-04-07 17:04:04', 'admin', 2, 'pending', '待阅', '/index/pending', 1, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (5, '2020-04-08 11:44:02', 'admin', 2, 'read', '已阅', '/index/read', 1, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (6, '2020-04-08 11:45:32', 'admin', 2, 'check', '查看', '/index/todo/check', 2, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (7, '2020-04-08 11:46:18', 'admin', 2, 'export', '导出', '/index/todo/export', 2, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (8, '2020-04-08 11:47:03', 'admin', 2, 'check', '查看', '/index/done/check', 3, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (9, '2020-04-08 11:47:39', 'admin', 2, 'export', '导出', '/index/done/export', 3, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (10, '2020-04-08 18:53:11', 'admin', 2, 'check', '查看', '/index/pending/check', 4, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (11, '2020-04-08 18:55:30', 'admin', 2, 'export', '导出', '/index/pending/export', 4, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (12, '2020-04-08 18:56:09', 'admin', 2, 'check', '查看', '/index/read/check', 5, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (13, '2020-04-08 18:56:56', 'admin', 2, 'export', '导出', '/index/read/export', 5, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (14, '2020-04-08 18:59:16', 'admin', 2, 'projects', '项目管理', '/projects', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (15, '2020-04-08 19:00:18', 'admin', 2, 'projectList', '我的项目', '/projects/list', 14, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (16, '2020-04-08 19:02:13', 'admin', 2, 'projectCreate', '新建', '/projects/list/create', 15, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (17, '2020-04-08 19:04:15', 'admin', 2, 'projectCheck', '查看', '/projects/list/check', 15, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (18, '2020-04-08 19:05:04', 'admin', 2, 'projectCheckOrEdit', '查看/编辑', '/projects/list/checkOrEdit', 15, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (19, '2020-04-08 19:06:26', 'admin', 2, 'projectDelete', '删除草稿', '/projects/list/delete', 15, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (20, '2020-04-08 19:07:19', 'admin', 2, 'projectCheckRecords', '查看关联询价记录', '/projects/list/checkRecords', 15, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (21, '2020-04-08 19:08:40', 'admin', 2, 'projectExport', '导出', '/projects/list/export', 15, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (22, '2020-04-08 19:09:58', 'admin', 2, 'goods', '货物管理', '/goods', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (23, '2020-04-08 19:10:46', 'admin', 2, 'goodsList', '我的货物', '/goods/list', 22, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (24, '2020-04-08 19:13:03', 'admin', 2, 'goodsCreate', '新建', '/goods/list/create', 23, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (25, '2020-04-08 19:16:05', 'admin', 2, 'goodsCheck', '查看', '/goods/list/check', 23, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (26, '2020-04-08 19:17:08', 'admin', 2, 'goodsCheckOrEdit', '查看/编辑', '/goods/list/checkOrEdit', 23, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (27, '2020-04-08 19:18:13', 'admin', 2, 'goodsDelete', '删除', '/goods/list/delete', 23, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (28, '2020-04-08 19:19:07', 'admin', 2, 'goodsExport', '导出', '/goods/list/export', 23, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (29, '2020-04-08 19:19:58', 'admin', 2, 'goodsDownload', '下载导入模板', '/goods/list/download', 23, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (30, '2020-04-08 19:21:09', 'admin', 2, 'goodsImport', '导入', '/goods/list/import', 23, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (31, '2020-04-08 19:22:08', 'admin', 2, 'prices', '询价管理', '/prices', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (32, '2020-04-08 19:23:44', 'admin', 2, 'pricesMylist', '我的询价记录', '/prices/mylist', 31, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (33, '2020-04-08 19:24:46', 'admin', 2, 'pricesAsk', '询价', '/prices/mylist/ask', 32, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (34, '2020-04-08 19:25:48', 'admin', 2, 'pricesCheck', '查看', '/prices/mylist/check', 32, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (35, '2020-04-08 19:26:31', 'admin', 2, 'pricesProject', '查看关联项目', '/prices/mylist/project', 32, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (36, '2020-04-08 19:27:20', 'admin', 2, 'pricesExport', '导出', '/prices/mylist/export', 32, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (37, '2020-04-08 19:28:06', 'admin', 2, 'supplier', '供应商管理', '/supplier', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (38, '2020-04-08 19:30:31', 'admin', 2, 'supplierList', '我的供应商', '/supplier/list', 37, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (39, '2020-04-08 19:31:27', 'admin', 2, 'supplierInfo', '我的信息', '/supplier/info', 37, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (40, '2020-04-08 19:32:49', 'admin', 2, 'supplierUpdate', '我的变更', '/supplier/update', 37, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (41, '2020-04-08 19:33:28', 'admin', 2, 'supplierReset-pwd', '密码重置', '/supplier/reset-pwd', 37, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (42, '2020-04-08 19:34:40', 'admin', 2, 'supplierCheck', '查看', '/supplier/list/check', 38, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (43, '2020-04-08 19:35:38', 'admin', 2, 'supplierExport', '导出', '/supplier/list/export', 38, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (44, '2020-04-08 19:36:19', 'admin', 2, 'supplierInfoCheck', '查看', '/supplier/info/check', 39, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (45, '2020-04-08 19:37:30', 'admin', 2, 'supplierUpdateCreate', '新建', '/supplier/update/create', 40, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (46, '2020-04-08 19:38:44', 'admin', 2, 'supplierReset-pwdReset', '重置', '/supplier/reset-pwd/reset', 41, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (47, '2020-04-08 19:39:44', 'admin', 2, 'agent', '代理商管理', '/agent', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (48, '2020-04-08 19:42:06', 'admin', 2, 'agentList', '我的代理商', '/agent/list', 47, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (49, '2020-04-08 19:42:58', 'admin', 2, 'agentCreate', '新建', '/agent/list/create', 48, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (50, '2020-04-08 19:44:35', 'admin', 2, 'agentCheckOrEdit', '查看/编辑', '/agent/list/checkOrEdit', 48, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (51, '2020-04-08 19:45:48', 'admin', 2, 'agentDelete', '删除', '/agent/list/delete', 48, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (52, '2020-04-08 19:46:19', 'admin', 2, 'agentExport', '导出', '/agent/list/export', 48, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (53, '2020-04-08 19:46:55', 'admin', 2, 'purchaseResults', '采购结果通知书', '/purchaseResults', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (54, '2020-04-08 19:48:14', 'admin', 2, 'purchaseResultsList', '我的采购结果通知书', '/purchaseResults/list', 53, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (55, '2020-04-08 19:49:04', 'admin', 2, 'purchaseResultsCheck', '查看', '/purchaseResults/list/check', 54, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (56, '2020-04-08 19:49:48', 'admin', 2, 'purchaseResultsExport', '导出', '/purchaseResults/list/export', 54, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (57, '2020-04-08 19:50:47', 'admin', 2, 'bids', '中标通知书', '/bids', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (58, '2020-04-08 19:51:19', 'admin', 2, 'bidsList', '我的中标通知书', '/bids/list', 57, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (59, '2020-04-08 19:52:06', 'admin', 2, 'bidsCheck', '查看', '/bids/list/check', 58, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (60, '2020-04-08 19:52:51', 'admin', 2, 'bidsExport', '导出', '/bids/list/export', 58, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (61, '2020-04-08 19:53:24', 'admin', 2, 'contract', '合同', '/contract', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (62, '2020-04-08 19:54:00', 'admin', 2, 'contractList', '我的合同', '/contract/list', 61, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (63, '2020-04-08 19:55:42', 'admin', 2, 'contractCheck', '查看', '/contract/list/check', 62, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (64, '2020-04-08 19:56:22', 'admin', 2, 'contractExport', '导出', '/contract/list/export', 62, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (65, '2020-04-08 19:57:50', 'admin', 2, 'template', '模板管理', '/template', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (66, '2020-04-08 19:58:29', 'admin', 2, 'templatePurchase', '采购结果通知书模板', '/template/purchase', 65, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (67, '2020-04-08 19:59:32', 'admin', 2, 'templateBids', '成交通知书模板', '/template/bids', 65, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (68, '2020-04-08 20:00:58', 'admin', 2, 'templateContract', '合同模板', '/template/contract', 65, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (69, '2020-04-08 20:02:43', 'admin', 2, 'templatePurchaseCreate', '新建', '/template/purchase/create', 66, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (70, '2020-04-08 20:04:17', 'admin', 2, 'templatePurchaseCheckOrEdit', '查看/编辑', '/template/purchase/checkOrEdit', 66, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (71, '2020-04-08 20:05:28', 'admin', 2, 'templatePurchaseExport', '导出', '/template/purchase/export', 66, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (72, '2020-04-08 20:07:43', 'admin', 2, 'templatePurchaseDelete', '删除', '/template/purchase/delete', 66, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (73, '2020-04-08 20:08:54', 'admin', 2, 'templateBidsCreate', '新建', '/template/bids/create', 67, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (74, '2020-04-08 20:10:55', 'admin', 2, 'templateBidsCheckOrEdit', '查看/编辑', '/template/bids/checkOrEdit', 67, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (75, '2020-04-08 20:12:04', 'admin', 2, 'templateBidsExport', '导出', '/template/bids/export', 67, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (76, '2020-04-08 20:12:49', 'admin', 2, 'templateBidsDelete', '删除', '/template/bids/delete', 67, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (77, '2020-04-08 20:13:25', 'admin', 2, 'templateContractCreate', '新建', '/template/contract/create', 68, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (78, '2020-04-08 20:14:27', 'admin', 2, 'templateContractCheckOrEdit', '查看/编辑', '/template/contract/checkOrEdit', 68, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (79, '2020-04-08 20:14:30', 'admin', 2, 'templateContractExport', '导出', '/template/contract/export', 68, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (80, '2020-04-08 20:17:28', 'admin', 2, 'templateContractDelete', '删除', '/template/contract/delete', 68, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (81, '2020-04-08 20:20:26', 'admin', 2, 'system', '系统设置', '/system', 0, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (82, '2020-04-08 20:17:28', 'admin', 2, 'systemUser', '用户管理', '/system/user', 81, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (83, '2020-04-08 20:17:28', 'admin', 2, 'systemRole', '角色管理', '/system/role', 81, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (84, '2020-04-08 20:20:34', 'admin', 2, 'systemUserCreate', '新建', '/system/user/create', 82, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (85, '2020-04-08 20:20:36', 'admin', 2, 'systemUserCheckOrEdit', '查看/编辑', '/system/user/checkOrEdit', 82, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (86, '2020-04-08 20:17:28', 'admin', 2, 'systemUserDelete', '删除', '/system/user/delete', 82, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (87, '2020-04-08 20:17:28', 'admin', 2, 'systemUserRoles', '角色配置', '/system/user/roles', 82, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (88, '2020-04-08 20:17:28', 'admin', 2, 'systemRoleCreate', '新建', '/system/role/create', 83, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (89, '2020-04-08 20:17:28', 'admin', 2, 'systemRoleCheckOrEdit', '查看/编辑', '/system/role/checkOrEdit', 83, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (90, '2020-04-08 20:17:28', 'admin', 2, 'systemRoleDelete', '删除', '/system/role/delete', 83, 1);
INSERT INTO `scms`.`permissions`(`perm_id`, `create_date`, `creator`, `is_delete`, `menu_code`, `menu_name`, `menu_url`, `parent_perm_id`, `status`) VALUES (91, '2020-04-08 20:17:28', 'admin', 2, 'systemRolePermissions', '权限配置', '/system/role/permissions', 83, 1);



INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 1);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 2);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 3);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 4);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 5);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 6);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 7);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 8);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 9);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 10);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 11);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 12);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 13);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 14);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 15);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 16);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 17);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 18);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 19);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 20);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 21);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 22);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 23);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 24);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 25);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 26);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 27);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 28);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 29);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 30);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 31);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 32);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 33);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 34);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 35);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 36);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 37);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 39);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 40);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 41);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 44);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 45);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 46);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 47);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 48);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 49);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 50);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 51);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 52);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 61);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 62);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 63);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (1, 64);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 1);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 2);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 3);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 4);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 5);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 6);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 7);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 8);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 9);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 10);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 11);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 12);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 13);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 61);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 62);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 63);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (2, 64);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 1);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 2);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 3);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 4);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 5);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 6);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 7);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 8);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 9);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 10);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 11);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 12);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 13);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 14);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 15);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 17);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 21);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 22);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 23);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 25);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 28);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 31);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 32);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 33);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 34);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 35);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 36);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 53);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 54);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 55);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (3, 56);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (4, 1);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (4, 4);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (4, 5);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (4, 10);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (4, 11);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (4, 12);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (4, 13);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 1);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 2);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 3);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 6);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 7);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 8);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 9);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 14);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 15);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 17);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 21);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 22);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 23);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 25);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 28);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 53);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 54);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 55);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 56);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 61);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 62);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 63);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (5, 64);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (6, 37);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (6, 38);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (6, 42);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (6, 43);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (6, 47);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (6, 48);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (6, 50);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 65);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 66);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 67);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 68);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 69);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 70);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 71);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 72);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 73);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 74);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 75);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 76);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 77);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 78);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 79);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 80);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 81);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 82);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 83);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 84);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 85);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 86);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 87);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 88);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 89);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 90);
INSERT INTO `scms`.`role_permis`(`role_id`, `perm_id`) VALUES (7, 91);



INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (1, '协议供货-采购结果通知书.xlsx', 85123, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '/data/nginx/html/protocol_supply/202003/e17464aa-ed75-46ee-acc9-1960d7d4150a.xlsx', '2020-03-23 02:23:20', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (2, '新建 Microsoft Excel 工作表.xlsx', 10725, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '/data/nginx/html/protocol_supply/202003/e1cf7ab5-8bdd-4e29-9bb9-ff8f267b06b1.xlsx', '2020-03-23 02:32:10', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (3, '新建 Microsoft Excel 工作表.xlsx', 10725, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '/data/nginx/html/protocol_supply/202003/9af0b9b4-71c9-4e91-a04f-3d33b4cf4cdd.xlsx', '2020-03-23 02:33:39', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (4, '协议供货-中标通知书.xlsx', 12350, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '/data/nginx/html/protocol_supply/202003/9809cc52-8e9e-48a2-8890-fc445283ac11.xlsx', '2020-03-23 02:52:43', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (5, '协议供货-中标通知书.xlsx', 12350, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '/data/nginx/html/protocol_supply/202003/ea73b662-8424-4e14-9fcb-c5f4b175f90f.xlsx', '2020-03-23 03:01:34', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (6, '合同模板.doc', 70144, 'application/msword', '/data/nginx/html/protocol_supply/202003/079213b5-339f-43a8-ac77-c032b39a2163.doc', '2020-03-23 03:13:22', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (7, '合同模板.doc', 70144, 'application/msword', '/data/nginx/html/protocol_supply/202003/3b15d30b-c379-4a53-8876-84497588499e.doc', '2020-03-23 03:14:30', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (8, 'point-blue.png', 1510, 'image/png', '/data/nginx/html/protocol_supply/202003/bf8f1786-9892-434b-b0d3-ab4dc0662f86.png', '2020-03-23 09:10:12', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (9, 'point-red.png', 1554, 'image/png', '/data/nginx/html/protocol_supply/202003/1048f8a7-27ec-4ed6-bffb-e173e54896e7.png', '2020-03-23 09:49:13', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (10, 'partInfo.xls', 25600, 'application/vnd.ms-excel', 'D:\\data\\nginx\\html\\protocol_supply\\202003/c44f1689-430c-4e00-8b23-dcf39306dbeb.xls', '2020-03-25 10:42:29', 'admin');
INSERT INTO `scms`.`attachment`(`attach_id`, `attach_name`, `attach_size`, `file_type`, `path`, `upload_time`, `uploader`) VALUES (11, 'point-blue.png', 1510, 'image/png', '/data/nginx/html/protocol_supply/202003/1da9362a-2288-4ac5-99bf-463a48fb0c56.png', '2020-03-27 03:30:32', 'admin');





