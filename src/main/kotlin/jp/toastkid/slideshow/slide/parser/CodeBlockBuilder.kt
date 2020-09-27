package jp.toastkid.slideshow.slide.parser

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import javax.swing.JComponent
import javax.swing.JScrollPane

class CodeBlockBuilder {

    private var isInCodeBlock = false

    private val code = StringBuilder()

    fun build(): JComponent? {
        isInCodeBlock = !isInCodeBlock
        if (!isInCodeBlock && code.isNotEmpty()) {
            val codeArea = RSyntaxTextArea()
            codeArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVA
            //KotlinHighlighter(codeArea).highlight()
            codeArea.isEditable = false
            codeArea.isFocusable = false
            codeArea.font = codeArea.font.deriveFont(48f)
            codeArea.text = code.toString()
            code.setLength(0)
            return JScrollPane(codeArea)
        }
        return null
    }

    fun append(line: String) {
        code.append(if (code.isNotEmpty()) LINE_SEPARATOR else "").append(line)
    }

    fun shouldAppend(line: String): Boolean {
        return isInCodeBlock && !line.startsWith("```")
    }

    companion object {

        private val LINE_SEPARATOR = System.lineSeparator()

    }
}