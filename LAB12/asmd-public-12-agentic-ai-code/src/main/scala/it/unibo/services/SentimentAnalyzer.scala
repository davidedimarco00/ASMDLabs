package it.unibo.services

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.service.{AiServices, UserMessage}

trait SentimentAnalyzer:
  @UserMessage(Array("Does {{it}} has a positive sentiment?"))
  def analyzeSentiment(text: String): Boolean

object SentimentAnalyzer:
  def createWith(llmModel: ChatModel): SentimentAnalyzer =
    AiServices.builder(classOf[SentimentAnalyzer])
      .chatModel(llmModel)
      .build()

@main
def testSentimentAnalyzer(): Unit =
  val model = OllamaChatModel.builder().baseUrl("http://localhost:11434")
    .modelName("gemma4:e2b")
    .build()
  val sentimentAnalyzer = SentimentAnalyzer.createWith(model)
  println(sentimentAnalyzer.analyzeSentiment("Scala is a great programming language!")) // true
  println(sentimentAnalyzer.analyzeSentiment("I don't like bugs in my code.")) // false