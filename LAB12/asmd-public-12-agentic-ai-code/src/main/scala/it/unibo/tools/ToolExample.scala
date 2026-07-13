package it.unibo.tools

import dev.langchain4j.agent.tool.{P, Tool, ToolSpecifications}
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.ollama.OllamaChatModel

import scala.collection.JavaConverters.{asJavaIterableConverter, asScalaBufferConverter}
object ToolExample:
  @Tool(value = Array("Tool description"))
  def tool(@P("first parameter") x: Any, @P("second parameter") y: Any): String =
    s"Tool called with x=$x and y=$y"

@main
def toolDescriptionGeneration(): Unit =
  val tools = ToolSpecifications.toolSpecificationsFrom(ToolExample).asScala.toList
  // to message
  val message = ChatRequest.builder()
    .messages(UserMessage.from("What is the sum of 5 and 3?"))
    .toolSpecifications(ToolSpecifications.toolSpecificationsFrom(ToolExample))
    .build()

  val model = OllamaChatModel.builder()
    .baseUrl("http://localhost:11434")
    .modelName("qwen3.5:4b")
    .logRequests(true)
    .logResponses(true)
    .build()

  model.chat(message)


