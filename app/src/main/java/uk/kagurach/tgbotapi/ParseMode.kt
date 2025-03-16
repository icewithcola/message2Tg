@file:Suppress("unused")

package uk.kagurach.tgbotapi

enum class ParseMode {
  MARKDOWN {
    override fun getName(): String = "MarkdownV2"
  },
  HTML {
    override fun getName(): String = "HTML"
  },
  LEGACY_MARKDOWN {
    override fun getName(): String = "Markdown"
  },
  NONE {
    override fun getName(): String? = null
  };

  abstract fun getName(): String?
}