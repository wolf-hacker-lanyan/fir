# 接口
## 数据库结构
聊天信息表，聊天记录表
## 业务流程
- 1.用户进入界面，加载历史客服消息接口^[1],点击人工客服开始排，开始聊天接口^[2],websocket链接^[3]
- 2.正常聊天通过websocket链接实现推送，websocket内需包含客服状态接口（排队信息等）
- 3.客户手动结束会话，超时机制，3分钟无回复结束对话
- 4.会话转接，客服通过转接接口^[4]将会话转接给其他客服

## 接口定义
### 历史客服消息接口 `POST` `/chat/chathis`  
    设计：入参token，根据token转userid，查聊天信息表联记录表，反聊天信息

### 开始聊天接口  `POST` `/chat/start`
    设计：入参token等 ，在聊天表新建数据，反创建websocket的token，token有过期机制，超时的聊天token不通过

### websocket接口 `WS` `/chat/socket?token=`
    设计：token由开始接口或其他需要的接口通过类（SocketTokenManager）方法调用创建，具有过期机制
    生命周期：Created：根据usertoken和token鉴权，根据token拿roomid，反room信息
            Message：根据json数据区分变化和聊天

