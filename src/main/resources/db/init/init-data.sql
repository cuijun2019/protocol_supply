alter table users MODIFY enabled bit(10);

INSERT INTO users (
	ID,
	create_time,
	enabled,
	fullname,
	OWNER,
	PASSWORD,
	telephone,
	update_time,
	username,
	attach_id
)
VALUES
	(
		1,
		now(),
		't',
		'监控人员',
		'广东宜通世纪股份科技有限公司',
		'$2a$10$Lq4xOF33YvM6voT15RuTw.0kmfTWOlFX/v4wM1n2OaRQDbBOVCPxi',
		'18319670614',
		now(),
		'admin',
		NULL
	);