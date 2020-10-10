package jp.toastkid.slideshow.slide.parser

import jp.toastkid.slideshow.slide.model.Slide
import jp.toastkid.slideshow.slide.model.SlideDeck
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Matcher
import java.util.regex.Pattern


class SlideDeckReader(private val pathToMarkdown: Path) {

    /** Slide builder.  */
    private var builder: Slide? = null

    /** Table builder.  */
    private var tableBuilder: TableBuilder? = null

    private var codeBlockBuilder = CodeBlockBuilder()

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
                                if (deck.containsBackground().not()) {
                                    deck.setBackground(it)
                                    return@forEach
                                }
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
                        builder?.addQuotedLines(line)
                        return@forEach
                    }
                    // Adding code block.
                    if (line.startsWith("```")) {
                        codeBlockBuilder.build()?.let {
                            builder?.add(it)
                        }
                        return@forEach
                    }
                    if (codeBlockBuilder.shouldAppend(line)) {
                        codeBlockBuilder.append(line)
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

        /** In-line image pattern.  */
        private val FOOTER_TEXT: Pattern = Pattern.compile("\\[footer\\]\\((.+?)\\)")
    }
}