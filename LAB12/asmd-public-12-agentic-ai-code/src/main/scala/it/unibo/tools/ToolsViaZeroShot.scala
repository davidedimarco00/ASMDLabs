package it.unibo.tools

import dev.langchain4j.agent.tool.{Tool, ToolSpecifications}
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.service.{AiServices, UserMessage as UserMessageAnnotation}
import dev.langchain4j.model.chat.request.{ChatRequest, ResponseFormat}
import dev.langchain4j.model.ollama.OllamaChatModel

import scala.jdk.CollectionConverters.*

trait MathAgent:
  @UserMessageAnnotation(Array("Help me to solve this math problem: {{it}}"))
  def solveMathProblem(problem: String): String

@main
def llmViaAiService(): Unit =
  val mathModule = new MathModule
  val tools = ToolSpecifications.toolSpecificationsFrom(mathModule).asScala.toList
  tools.foreach(tool => println(tool))

  val messageToSend = ChatRequest.builder()
    .messages(UserMessage.from("What is the sum of 5 and 3?"))
    .toolSpecifications(tools.asJava)
    .build()

  val model = OllamaChatModel.builder()
    .baseUrl("http://localhost:11434")
    .modelName("qwen3.5:4b")
    .logRequests(true)
    .logResponses(true)
    .build()
  println(messageToSend)

  val aiService = AiServices.builder(classOf[MathAgent])
    .tools(mathModule)
    .chatModel(model)
    .build()

  val result = aiService.solveMathProblem("sum of 5 and 3 product of 4 and 2")
  println(s"Result: $result")