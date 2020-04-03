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