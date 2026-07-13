package it.unibo.memory

import dev.langchain4j.memory.ChatMemory
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.service.{AiServices, UserMessage}

trait Assistant:
  @UserMessage(Array("You are a helpful assistant that can remember previous interactions. Answer the following message: {{it}}"))
  def chat(message: String): String

@main
def testMemory(): Unit =
  val assistant = OllamaChatModel.builder()
    .baseUrl("http://localhost:11434")
    .modelName("gemma4:e2b")
    .logRequests(true)
    .logResponses(true)
    .build()

  val chatMemory: ChatMemory = MessageWindowChatMemory.withMaxMessages(10)
  val service = AiServices.builder(classOf[Assistant])
    .chatMemory(chatMemory)
    .chatModel(assistant).build()

  service.chat("My name is Gianluca!")
  println(service.chat("What is my name?"))