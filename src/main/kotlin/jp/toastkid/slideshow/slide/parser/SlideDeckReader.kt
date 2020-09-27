package jp.toastkid.slideshow.slide.parser

import jp.toastkid.slideshow.slide.model.Slide
import jp.toastkid.slideshow.slide.model.SlideDeck
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.swing.JScrollPane


class SlideDeckReader(private val pathToMarkdown: Path) {

    /** Slide builder.  */
    private var builder: Slide? = null

    /** Table builder.  */
    private var tableBuilder: TableBuilder? = null

    /** Code block processing.  */
    private var isInCodeBlock = false

    private val imageExtractor = ImageExtractor()

    private val backgroundExtractor = BackgroundExtractor()

    /**
     * Init with source's path.
     */
    init {
        builder = Slide()
    }

    /**
     * Convert to Slides.
     * @return List&lt;Slide&gt;
     */
    operator fun invoke(): SlideDeck {
        val deck = SlideDeck()
        try {
            Files.lines(pathToMarkdown).use { lines ->
                val code = StringBuilder()
                lines.forEach { line: String ->
                    if (line.startsWith("#")) {
                        if (builder?.hasTitle() == true) {
                            builder?.let {
                                deck.add(it)
                            }
                            builder = Slide()
                        }
                        if (line.startsWith("# ")) {
                            builder?.isFront(true)
                        }
                        builder?.setTitle(line.substring(line.indexOf(" ")).trim { it <= ' ' })
                        return@forEach
                    }
                    if (line.startsWith("![")) {
                        if (BackgroundExtractor.shouldInvoke(line)) {
                            backgroundExtractor(line)?.let {
                                builder?.setBackground(it)
                            }
                            return@forEach
                        }

                        builder?.add(imageExtractor.invoke(line))
                        return@forEach
                    }
                    if (line.startsWith("[footer](")) {
                        val matcher: Matcher = FOOTER_TEXT.matcher(line)
                        if (matcher.find()) {
                            deck.setFooterText(matcher.group(1))
                        }
                        return@forEach
                    }
                    if (line.startsWith("> ")) {
                        //builder.addQuotedLines(line)
                        return@forEach
                    }
                    // Adding code block.
                    if (line.startsWith("```")) {
                        isInCodeBlock = !isInCodeBlock
                        if (!isInCodeBlock && code.isNotEmpty()) {
                            val codeArea = RSyntaxTextArea()
                            codeArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_JAVA
                            //KotlinHighlighter(codeArea).highlight()
                            codeArea.isEditable = false
                            codeArea.isFocusable = false
                            codeArea.font = codeArea.font.deriveFont(48f)
                            codeArea.text = code.toString()
                            builder?.add(JScrollPane(codeArea))
                            code.setLength(0)
                        }
                        return@forEach
                    }
                    if (isInCodeBlock && !line.startsWith("```")) {
                        code.append(if (code.isNotEmpty()) LINE_SEPARATOR else "").append(line)
                        return@forEach
                    }

                    if (TableBuilder.isTableStart(line)) {
                        if (tableBuilder == null) {
                            tableBuilder = TableBuilder()
                        }

                        if (TableBuilder.shouldIgnoreLine(line)) {
                            return@forEach
                        }

                        if (tableBuilder?.hasColumns() == false) {
                            tableBuilder?.setColumns(line)
                            return@forEach
                        }

                        tableBuilder?.addTableLines(line)
                        return@forEach
                    }

                    if (tableBuilder != null) {
                        tableBuilder?.get()?.let {
                            builder?.add(it)
                        }
                        tableBuilder = null
                    }
                    // Not code.
                    if (line.isNotEmpty()) {
                        builder?.addText(line)
                    }
                }
                builder?.let {
                    deck.add(it)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return deck
    }

    companion object {

        private val LINE_SEPARATOR = System.lineSeparator()

        /** In-line image pattern.  */
        private val FOOTER_TEXT: Pattern = Pattern.compile("\\[footer\\]\\((.+?)\\)")
    }
}