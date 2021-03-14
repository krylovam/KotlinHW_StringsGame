import java.io.File
import kotlin.math.abs
import kotlin.random.Random
import kotlinx.coroutines.*

fun main() {
    val minLength = 8
    val fileName = "words.txt"
    val userWordsFileName = "user_words.txt"
    val wordSet = mutableSetOf<String>()
    val longWordsList = mutableListOf<String>()
    runBlocking {
        File(fileName).useLines { lines -> wordSet.addAll(lines) }
        for (word in wordSet) {
            if (word.length > minLength) {
                longWordsList.add(word)
            }
        }

        val randWord = longWordsList[abs(Random.nextInt()) % longWordsList.size]
        println("Составьте из букв слова \"$randWord\" как можно больше слов " +
                "\nСлова вписывать через пробел" +
                "\nПо окончании нажмите Enter")
        val letterMap = mutableMapOf<Char, Int>()
        for (letter in randWord) {
            if (letterMap[letter] != null) {
                letterMap[letter] = letterMap[letter]!! + 1
            } else {
                letterMap[letter] = 1
            }
        }
        val userLine = readLine()
        if (userLine != null) {
            val userWords = userLine.split(" ").toTypedArray()
            val correctUserWords = mutableListOf<String>()
            for (word in userWords) {
                val currLetterMap = letterMap.toMutableMap()
                var isCorrectWord = true
                for (letter in word) {
                    if (currLetterMap[letter] === null) {
                        isCorrectWord = false
                        break
                    }
                    currLetterMap[letter] = currLetterMap[letter]!! - 1
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

