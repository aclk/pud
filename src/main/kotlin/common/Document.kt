package common

import java.io.File


interface Document {
    fun buildDocContent(): String

    fun exportDocCompleted() = run { }

    infix fun exportDoc(filePath: String) {
        generateDoc(filePath)
    }

    private fun generateDoc(filePath: String) {
        val content = buildDocContent()
        println(
            """
            |================================
            |   $content
            |================================
            |   $filePath
        """.trimMargin()
        )
        File(filePath).apply { parentFile.mkdirs() }.writeText(content)
        exportDocCompleted()
    }
}