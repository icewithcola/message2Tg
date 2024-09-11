# Simple Message Forwarder
# [中文说明](./README_CN.MD)
## Usage
- Fill `Bot Token`
- Fill `Chat Id` (can be filled automatically by clicking button and send any message to the bot)
- Press `Go!` Button. App will send you a message and service will run background

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
- XiaoMi: In `Message` program, disable verification code protection