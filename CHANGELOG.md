# Changelog

All notable changes to this project are documented in this file.

## [1.6.3] - 2026-09-06

### Changed

- Improved AI-powered SMS classification with locale-aware intent labels and summaries.
- Refactored AI verification-code extraction to build structured JSON requests and use clearer system and user prompts.
- Improved forwarded-message formatting by placing verification codes before sender details and separating summaries from the original message.

### Fixed

- Prevented blank fields and literal `null` values returned by AI providers from appearing in Telegram messages.
