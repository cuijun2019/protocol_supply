# protocol_supply
供货协议系统

### 服务器
```
IP：192.168.8.176
用户名：root
密码：etone3edc$RFV
```

### 部署流程
1. cd /hainan/protocol_supply
2. 替换protocol_supply-1.0.0-SNAPSHOT.jar
3. docker-compose down
4. docker rmi protocol_supply
5. docker build -t protocol_supply .
6. docker-compose up -d
