package it.unibo.services

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ResponseFormat
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.service.{AiServices, UserMessage}
import it.unibo.Move
import it.unibo.services.TicTacToe.{AIPlayer, Board, Player}


object TicTacToe:

  val size = 3
  enum Player:
    case X, O

  enum GameResult:
    case Win(player: Player)
    case Draw
    case Ongoing
  type Cell = (Int, Int, Player)

  type Board = List[Cell]

  def gameStatus(board: Board): GameResult =
    val cells = board.map((row, col, player) => (row, col) -> player).toMap
    val rows = List.tabulate(size, size)((r, c) => (r, c))
    val cols  = List.tabulate(size, size)((c, r) => (r, c))
    val diags = List(List.tabulate(size)(i => (i, i)), List.tabulate(size)(i => (i, size - 1 - i)))
    val winningLines = rows ++ cols ++ diags

    def winnerOn(line: List[(Int, Int)]): Option[Player] =
      val linePlayers = line.flatMap(cells.get)
      if linePlayers.size == size && linePlayers.distinct.size == 1 then
        Some(linePlayers.head)
      else None

    winningLines
      .map(winnerOn)
      .collectFirst { case Some(player) => GameResult.Win(player) }
      .getOrElse:
        if cells.size == size * size then GameResult.Draw
        else GameResult.Ongoing

  def renderGame(board: Board): String =
    val cells = board.map((row, col, player) => (row, col) -> player).toMap
    val renderedRows = (0 until size).map { r =>
      (0 until size).map { c =>
        cells.get((r, c)) match
          case Some(Player.X) => "X"
          case Some(Player.O) => "O"
          case None => " "
      }.mkString("|")
    }
    renderedRows.mkString("\n" + "-" * (size * 2 - 1) + "\n")


  trait AIPlayer:
    @UserMessage(Array("Given the current board state, what is your next move? row >= 0 <= 2, col >= 0 <= 2. Board state: {{board}}"))
    def nextMove(board: Board): Move

  object AIPlayer:
    def creatWith(model: ChatModel): AIPlayer =
      AiServices.builder(classOf[AIPlayer])
        .chatModel(model)
        .build()

@main
def testTicTacToe(): Unit =
  val model = OllamaChatModel.builder()
    .baseUrl("http://localhost:11434")
    .modelName("gemma4:e2b")
    .responseFormat(ResponseFormat.JSON)
    .build()

  val aiPlayer = AIPlayer.creatWith(model)
  var board: Board = List()

  while(TicTacToe.gameStatus(board) == TicTacToe.GameResult.Ongoing) do
    val move = aiPlayer.nextMove(board)
    // your move:
    println(s"AI plays: $move")
    // new board state:
    board = board :+ (move.row, move.col, Player.O)
    println(TicTacToe.renderGame(board))
    println("---")
    println("Your turn! Enter row and column (0-based, separated by space):")
    val input = scala.io.StdIn.readLine()
    val Array(row, col) = input.split(" ").map(_.toInt)
    board = board :+ (row, col, Player.X)




