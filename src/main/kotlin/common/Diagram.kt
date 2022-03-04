package common

import common.Diagram.Color.BLACK
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils
import java.io.File
import java.io.FileOutputStream

interface Diagram {
    enum class LayoutDirection {
        Horizontal,
        Vertical
    }

    companion object {
        const val ASSOCIATE: String = """ -[${BLACK}]-> """
        const val POSITION: String = """ -[hidden]- """
    }

    fun buildPlantUmlString(): String

    fun exportResult(isSuccess: Boolean) = run { }

    infix fun exportDiagram(filePath: String) {
        generateDiagram(filePath)
    }

    /**
     * skinparam backgroundColor transparent
     * skinparam defaultFontColor White
     * skinparam arrowFontColor Black
     * skinparam roundCorner 10
     * hide circle equals skinparam style strictuml
     * skinparam roundCorner 10
     **/
    fun getClassStyle(layoutDirection: LayoutDirection = LayoutDirection.Vertical): String {
        return """
        |skinparam class {
        |   BorderColor black
        |   FontColor White
        |   AttributeFontColor White
        |   StereotypeFontColor White
        |}
        |${if (layoutDirection == LayoutDirection.Vertical) "left to right direction" else ""}
        |skinparam defaultTextAlignment center
        |skinparam style strictuml
        |hide empty members
        """.trimIndent()
    }

    fun getRectangleStyle(): String {
        return """
        |skinparam rectangle {
        |   BorderColor black
        |   FontColor White
        |   BackgroundColor White
        |}
        |skinparam defaultTextAlignment center
        """.trimIndent()
    }

    private fun generateDiagram(filePath: String): Boolean {
        val plantumlStr = buildPlantUmlString()
        println(
            """
            |================================
            |   $plantumlStr
            |================================
            |   $filePath
        """.trimMargin()
        )

        val file = File(filePath).apply { parentFile.mkdirs() }
        GraphvizUtils.setLocalImageLimit(10000)
        with(
            SourceStringReader(plantumlStr).outputImage(
                FileOutputStream(file),
                FileFormatOption(getFileType(filePath))
            ).description != null
        ) {
            return apply { exportResult(this) }
        }
    }

    private fun getFileType(filePath: String): FileFormat = when (File(filePath).extension) {
        "svg" -> FileFormat.SVG
        "png" -> FileFormat.PNG
        else -> throw IllegalArgumentException("file format error, format: ${File(filePath).extension}")
    }


    interface KeyInfo<T> : DSL<T> {
        fun timestamp(vararg timestamps: String)

        fun data(vararg data: String)
    }

    object Color {
        const val PINK = "#F0637C"
        const val GREEN = "#6D9D79"
        const val YELLOW = "#CA8522"
        const val PURPLE = "#63507C"
        const val BLUE = "#4BA1AC"
        const val WAVE_BLUE = "#043D4E"
        const val GREY = "#EDF1F3"
        const val DARK_GREY = "#Gray"
        const val BLACK = "#000000"
        const val WHITE = "#FFFFFF"
        const val TRANSPARENT = "#transparent"
    }
}