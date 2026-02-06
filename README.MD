# Simple Message Forwarder
# [中文说明](./README_CN.MD)

Simple Message Forwarder is an application for forwarding SMS to Telegram with its bot api.


## Screenshots
![homepage](./docs/homepage.png)
![settings](./docs/settings.png)
![message](./docs/message.png)

## Usage
- Fill `Bot Token`
- Fill `Chat Id` (can be filled automatically by clicking button and send any message to the bot)
- Press `Go!` Button. App will send you a message and service will run background

## Commands
- `/getinfo` Get current battery status.
- `/getLastMessage <1..5>` Get last 1 to 5 (inclusive) messages

## AI-Powered Verification Code Extraction
This application supports extracting verification codes using an AI service that is compatible with the OpenAI API specification. If configured, the app will prioritize using AI for extraction.

### How to Configure
1.  Go to "Advanced Settings" from the main screen.
2.  Fill in the following three fields:
    *   **AI Endpoint**: The endpoint address of your AI service. Please note that you should enter the **base address** here (e.g., `https://api.openai.com` or another compatible API address). The application will automatically append `/v1/chat/completions` to it.
    *   **AI Api Key**: Your API key.
    *   **AI Model**: The name of the model you wish to use (e.g., `gpt-3.5-turbo`).
3.  After saving the settings, newly received SMS messages will attempt to extract the verification code through your configured AI service.

### API Specification
Your AI service needs to be compatible with the OpenAI [Chat Completions API](https://platform.openai.com/docs/api-reference/chat) format. The application will send a `POST` request with a `JSON` payload structured as follows:

```json
{
  "model": "The model name you entered",
  "messages": [
    {
      "role": "user",
      "content": "Extract the verification code from this message: ..."
    }
  ]
}
```

The server should return a `JSON` response containing the verification code.

## Develop
Put your key under `./app` directory with name `key.jks`, then fill `local.properties` with
```
keyStorePassword=yourpassword
keyAlias=yourkeyalias
keyPassword=yourkeypassword
```
Another way is to export some enviroment variables, see `build.gradle.kts`.\
At last, run
`./gradlew :app:assembleRelease`


## Message Example
```Markdown
From: ${Sender}
Verification code: `${VerificationCode?}`
Original text:
>${MessageBody}
```

## Device Specific Configuration
- XiaoMi: In `Message` program, disable verification code protection. Also give every SMS-Related permissions.