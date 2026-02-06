# Simple Message Forwarder
# [English Version](./README.MD)

Simple Message Forwarder 是一个将短信转发到 Telegram 的小型软件。


## 截图
![homepage](./docs/homepage.png)
![settings](./docs/settings.png)
![message](./docs/message.png)

## 使用方法
- 填入 `Bot Token`
- 填入 `Chat Id` (亦可点击右边按钮，向您的机器人发送任何消息，该信息可以被自动填入)
- 点击 `开始` 按钮，服务将会给您发送一个消息，并在后台运行

## 命令
- `/getinfo` 获取当前手机电量
- `/getLastMessage <1..5>` 获取最后 1~5 条消息

## AI 提取验证码
本应用支持通过符合 OpenAI API 规范的 AI 服务来提取验证码。如果配置了此功能，应用会优先使用 AI 进行提取。

### 配置方法
1.  在主界面进入“高级设置”。
2.  填写以下三个字段：
    *   **AI Endpoint**: 您的 AI 服务端点地址。请注意，这里应该填写 **基础地址**（例如 `https://api.openai.com` 或其他兼容的 API 地址），应用会自动在后面拼接 `/v1/chat/completions`。
    *   **AI Api Key**: 您的 API 密钥。
    *   **AI Model**: 您希望使用的模型名称（例如 `gpt-3.5-turbo`）。
3.  保存设置后，新接收到的短信将尝试通过您配置的 AI 服务来提取验证码。

### API 规范
您的 AI 服务需要与 OpenAI 的 [Chat Completions API](https://platform.openai.com/docs/api-reference/chat) 格式兼容。应用会发送一个包含以下结构 `JSON` 的 `POST` 请求：

```json
{
  "model": "您填写的模型名称",
  "messages": [
    {
      "role": "user",
      "content": "Extract the verification code from this message: ..."
    }
  ]
}
```

服务器应返回一个包含验证码的 `JSON` 响应。

## 开发
把你的密钥放在 `./app` 文件夹下，并命名为 `key.jks`，然后填写 `local.properties`
```
keyStorePassword=yourpassword
keyAlias=yourkeyalias
keyPassword=yourkeypassword
```
或者可以使用环境变量，详见 `build.gradle.kts`.\
最后
`./gradlew :app:assembleRelease`

## 消息实例
```Markdown
来自： ${Sender}
验证码：`${VerificationCode?}`
原消息：
>${MessageBody}
```

## 在部分设备上的注意事项
- 小米：关闭系统`信息`应用的验证码保护，并手动授予读取通知类短信的权限。