package jp.toastkid.slideshow.slide.parser

import jp.toastkid.slideshow.slide.model.Slide
import jp.toastkid.slideshow.slide.model.SlideDeck
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JLabel

class SlideDeckReader(private val pathToMarkdown: Path) {

    /** Slide builder.  */
    private var builder: Slide? = null

    /** Table builder.  */
    private var tableBuilder: TableBuilder? = null

    /** Code block processing.  */
    private var isInCodeBlock = false

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
                        if (line.startsWith("![background](")) {
                            extractBackgroundUrl(line)?.let {
                                builder?.setBackground(it)
                            }
                            return@forEach
                        }
                        extractImageUrls(line)
                                .filterNotNull()
                                .map {
                                    val img: BufferedImage = ImageIO.read(File(it))
                                    val icon = ImageIcon(img)
                                    JLabel(icon)
                                }
                                .forEach {
                                    builder?.add(it)
                                }
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
                            codeArea.font = codeArea.font.deriveFont(48f)
                            codeArea.text = code.toString()
                            builder?.add(codeArea)
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
                    if (tableBuilder != null || !line.startsWith("")) {
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

    /**
     * Extract background image url from text.
     * @param line line
     * @return image url
     */
    private fun extractBackgroundUrl(line: String): String? {
        val matcher: Matcher = BACKGROUND.matcher(line)
        return if (!matcher.find()) {
            null
        } else matcher.group(1)
    }

    /**
     * Extract image url from text.
     * @param line line
     * @return image url
     */
    private fun extractImageUrls(line: String): List<String?> {
        val imageUrls: MutableList<String?> = ArrayList()
        val matcher: Matcher = IMAGE.matcher(line)
        while (matcher.find()) {
            imageUrls.add(matcher.group(2))
        }
        return imageUrls
    }

    companion object {

        private val LINE_SEPARATOR = System.lineSeparator()

        /** Background image pattern.  */
        private val BACKGROUND: Pattern = Pattern.compile("\\!\\[background\\]\\((.+?)\\)")

        /** In-line image pattern.  */
        private val IMAGE: Pattern = Pattern.compile("\\!\\[(.?)\\]\\((.+?)\\)")

        /** In-line image pattern.  */
        private val FOOTER_TEXT: Pattern = Pattern.compile("\\[footer\\]\\((.+?)\\)")
    }
}