package connectfour

val reg = Regex("[0-9]++")

fun main() {
    println("Connect Four")
    val players = readPlayers()
    val dimensions = readDimensions()
    val numberOfGames = readNumberOfGames()
    writeGameParams(players, dimensions)
    setNumberOfGames(numberOfGames)
    promptTurns(players, dimensions, numberOfGames)
}

fun readNumberOfGames(): Int {
    while (true) {
        println(
            "Do you want to play single or multiple games?\n" +
                    "For a single game, input 1 or press Enter\n" +
                    "Input a number of games:"
        )
        val ln = readln().trim()
        if ((ln.matches(reg) && ln.toInt() != 0)) {
            return ln.toInt()
        } else {
            if (ln == "") {
                return 1
            } else {
                println("Invalid input")
            }
        }
    }
}

fun setNumberOfGames(n: Int) {
    if (n == 1) {
        println("Single game")
    } else {
        println("Total $n games")
    }
}

fun readPlayers() =
    listOf("First", "Second").map {
        println("$it player's name:")
        readln().trim()
    }

fun readDimensions(): List<Int> {
    while (true) {
        println("Set the board dimensions (Rows x Columns)\n" +
                "Press Enter for default (6 x 7)")
        val dim = readln().trim().lowercase()


        if (dim == "") {
            return listOf(6, 7)
        } else if (dim.split("x").size == 2 &&
            dim.split("x")[0].trim().matches(reg) &&
            dim.split("x")[1].trim().matches(reg)) {
            if (dim.split("x")[0].trim().toInt() !in 5..9) {
                println("Board rows should be from 5 to 9")
                continue
            } else if (dim.split("x")[1].trim().toInt() !in 5..9) {
                println("Board columns should be from 5 to 9")
                continue
            } else {
                val r = dim.split("x")[0].trim().toInt()
                val c = dim.split("x")[1].trim().toInt()
                return listOf(r, c)
            }
        } else {
            println("Invalid input")
        }
    }
}

fun writeGameParams(players: List<String>, dimensions: List<Int>) {
    println("${players[0]} VS ${players[1]}")
    println("${dimensions[0]} X ${dimensions[1]} board")
}

fun drawBoard(dimensions: List<Int>,
              filledPositions: MutableMap<Int, MutableList<MutableList<Int>>>): MutableList<MutableList<String>> {
    val r = dimensions[0]
    val c = dimensions[1]
    var n = 1
    for (i in 1..c) {
        print(" $n")
        n++
    }
    print("\n")
    val boardRCList = MutableList(r) { MutableList(c) { "║ " } }
    for (i in 0 until boardRCList.size) {
        boardRCList[i].add("║")
    }
    for (i in filledPositions[0]!!) {
        boardRCList[i[0]][i[1]] = "║o"
    }
    for (i in filledPositions[1]!!) {
        boardRCList[i[0]][i[1]] = "║*"
    }
    val boardBottomList = MutableList(c) { "╩═" }
    boardBottomList[0] = "╚═"
    boardBottomList.add("╝")

    for (i in 0 until boardRCList.size) {
        for (j in 0 until boardRCList[i].size) {
            print(boardRCList[i][j])
        }
        println()
    }
    for (i in boardBottomList) {
        print(i)
    }
    println()
    return (boardRCList)
}

fun promptTurns(players: List<String>, dimensions: List<Int>, numberOfGames: Int) {
    var p = 0

    val r = dimensions[0]
    val c = dimensions[1]
    var i = numberOfGames

    var s0 = 0 //1st player's score
    var s1 = 0 //2nd player's score

    MultipleGamesLoop@while (i != 0) { //Loop for all games
        val filledPositions = mutableMapOf(
            0 to emptyList<MutableList<Int>>().toMutableList(),
            1 to emptyList<MutableList<Int>>().toMutableList()
        )
        val availableColumns = MutableList(r) { (1..c).toMutableList() }
        if (numberOfGames > 1) {
            println("Game #${numberOfGames - i +1}")
        }
        drawBoard(dimensions, filledPositions)

        while (true) { //Loop for current game
            println("${players[p]}'s turn:")
            var currentRow = r - 1
            val answer = readln().trim()
            if (answer != "end") {
                if (answer.matches(reg)) {
                    if (answer.toInt() !in 1..c) {
                        println("The column number is out of range (1 - $c)")

                    } else {
                        val currentColumn = answer.toInt()
                        if (availableColumns[r - 1][currentColumn - 1] == 0) {
                            println("Column $currentColumn is full")
                            continue
                        }
                        for (i in 0 until r) {
                            if (currentColumn in availableColumns[i]) {
                                currentRow -= i
                                availableColumns[i][currentColumn - 1] = 0
                                break
                            }
                        }

                        filledPositions[p]?.add(mutableListOf(currentRow, currentColumn - 1))
                        val boardRCListGamed = drawBoard(dimensions, filledPositions)
                        p = if (p == 0) {
                            1
                        } else {
                            0
                        }
                        when (isResult(boardRCListGamed)) {
                            0 -> {
                                s0 += 2
                                println("Player ${players[0]} won")
                                println("Score")
                                println("${players[0]}: $s0 ${players[1]}: $s1")
                                i--
                                break
                            }
                            1 -> {
                                s1 += 2
                                println("Player ${players[1]} won")
                                println("Score")
                                println("${players[0]}: $s0 ${players[1]}: $s1")
                                i--
                                break
                            }
                            2 -> {
                                s0 += 1
                                s1 += 1
                                println("It is a draw")
                                println("Score")
                                println("${players[0]}: $s0 ${players[1]}: $s1")
                                i--
                                break
                            }
                        }
                    }
                } else {
                    println("Incorrect column number")
                }
            } else {
                println("Game over!")
                break@MultipleGamesLoop
            }
        }
        if (i == 0) {
            println("Game over!")
        }
    }
}

fun isResult (boardRCListGamed: MutableList<MutableList<String>>): Int {
    var h = ""; var v = ""; var fb = ""
    var result = 3
    if (boardRCListGamed.joinToString().contains("║ ")) {
        // check vertical
        for (i in 0 until boardRCListGamed[0].size) {
            for (j in boardRCListGamed.size-1 downTo 0) {
                v += boardRCListGamed[j][i]
                if (v.contains("║o║o║o║o")) {
                    result = 0
                } else if (v.contains("║*║*║*║*")) {
                    result = 1
                }
            }
        }
        // check horizontal
        for (i in boardRCListGamed.size-1 downTo 0) {
            for (j in 0 until boardRCListGamed[0].size) {
                h += boardRCListGamed[i][j]
                if (h.contains("║o║o║o║o")) {
                    result = 0
                } else if (h.contains("║*║*║*║*")) {
                    result = 1
                }
                fb += boardRCListGamed[i][j]
            }
        }

        // check diagonal
        for (i in 0 until boardRCListGamed.size - 3) {
            for (j in 0 until boardRCListGamed[i].size) {
                if (j < boardRCListGamed[i].size - 3 &&
                    boardRCListGamed[i][j] == boardRCListGamed[i + 1][j + 1] &&
                    boardRCListGamed[i][j] == boardRCListGamed[i + 2][j + 2] &&
                    boardRCListGamed[i][j] == boardRCListGamed[i + 3][j + 3]) {
                    when (boardRCListGamed[i][j]) {
                        "║o" -> result = 0
                        "║*" -> result = 1
                    }
                }
                if (j > 2 &&
                    boardRCListGamed[i][j] == boardRCListGamed[i + 1][j - 1] &&
                    boardRCListGamed[i][j] == boardRCListGamed[i + 2][j - 2] &&
                    boardRCListGamed[i][j] == boardRCListGamed[i + 3][j - 3]) {
                    when (boardRCListGamed[i][j]) {
                        "║o" -> result = 0
                        "║*" -> result = 1
                    }
                }
            }
        }
    }
    else {
        result = 2 //draw result
    }
    return result
}
