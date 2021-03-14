import java.io.File
import kotlin.math.abs
import kotlin.random.Random
import kotlinx.coroutines.*

fun main() {
    val minLength = 8
    val fileName = "words.txt"
    val userWordsFileName = "user_words.txt"
    var wordSet = mutableSetOf<String>()
    var longWordsList = mutableListOf<String>()
    runBlocking {
        File(fileName).useLines { lines -> wordSet.addAll(lines) }
        for (word in wordSet) {
            if (word.length > minLength) {
                longWordsList.add(word)
            }
        }

        val randWord = longWordsList.get(abs(Random.nextInt()) % longWordsList.size)
        println("\"$randWord\"\nСоставьте из букв слова как можно больше слов через пробел")
        println("Когда закончите, нажмите Enter")
        var letterMap = mutableMapOf<Char, Int>()
        for (letter in randWord) {
            if (letterMap[letter] != null) {
                letterMap.put(letter, letterMap[letter]!! + 1)
            } else {
                letterMap.put(letter, 1)
            }
        }
        val userLine = readLine()
        if (userLine != null) {
            val userWords = userLine.split(" ").toTypedArray()
            var correctUserWords = mutableListOf<String>()
            for (word in userWords) {
                var currLetterMap = letterMap.toMutableMap()
                var isCorrectWord = true
                for (letter in word) {
                    if (currLetterMap[letter] === null) {
                        isCorrectWord = false
                        break
                    }
                    currLetterMap.put(letter, currLetterMap[letter]!! - 1)
                    if (currLetterMap[letter]!! < 0) {
                        isCorrectWord = false
                        break
                    }
                }
                if (!isCorrectWord) {
                    println("Слово \"$word\" использует буквы, которых нет в изначальном слове")
                } else {
                    correctUserWords.add(word)
                }

            }

            val saveToFile = CoroutineScope(Dispatchers.IO).launch {
                var text = String()
                for (i in correctUserWords)
                {
                    text += i
                    text += "\n"
                }
                File(userWordsFileName).writeText(text)
            }
            val findWords = CoroutineScope(Dispatchers.Default).launch {
                var score = 0
                correctUserWords.forEach { word ->
                    if (wordSet.contains(word)) {
                        score += word.length
                    }
                    else{
                        println("Слова \"$word\" нет в нашем словаре")
                    }
                }
                println("Поздравляем! Вы набрали $score очков.")
            }
            saveToFile.join()
            findWords.join()
        }
    }
}

