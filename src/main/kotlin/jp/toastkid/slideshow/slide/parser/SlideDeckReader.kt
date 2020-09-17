package jp.toastkid.slideshow.slide.parser

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import jp.toastkid.slideshow.slide.model.Slide
import jp.toastkid.slideshow.slide.model.SlideDeck
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
import javax.swing.JTextArea

class SlideDeckReader(private val pathToMarkdown: Path) {

    /** Slide builder.  */
    private var builder: Slide? = null

    /** Table builder.  */
    //private var tableBuilder: TableBuilder? = null

    /** Code block processing.  */
    private var isInCodeBlock = false

    /** Table processing.  */
    private var isInTable = false

    /**
     * Init with source's path.
     * @param p
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
                            // TODO builder.isFront(true)
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
                        val images = HBox()
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
                        if (images.children.size == 0) {
                            return@forEach
                        }
                        images.alignment = Pos.CENTER
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
                        if (!isInCodeBlock && code.length != 0) {
                            val codeArea = JTextArea()
                            //KotlinHighlighter(codeArea).highlight()
                            codeArea.setEditable(false)
                            codeArea.font = codeArea.font.deriveFont(60f)
                            //codeArea.setStyle("-fx-font-size: 40pt;")
                            codeArea.text = code.toString()
                            builder?.add(codeArea)
                            code.setLength(0)
                        }
                        return@forEach
                    }
                    if (isInCodeBlock && !line.startsWith("```")) {
                        code.append(if (code.length != 0) LINE_SEPARATOR else "").append(line)
                        return@forEach
                    }
                    if (line.startsWith("|")) {
                        if (!isInTable) {
                            isInTable = true
                            // TODO tableBuilder = TableBuilder()
                        }
                        if (line.startsWith("|:---")) {
                            return@forEach
                        }
                        /* TODO if (!tableBuilder.hasColumns()) {
                                        tableBuilder.setColumns(convertTableColumns(line))
                                        return@forEach
                                    }*/
                        val split = line.split("\\|".toRegex()).toTypedArray()
                        //tableBuilder.addTableLine(Arrays.asList(*split).subList(1, split.size))
                        return@forEach
                    }
                    if (isInTable || !line.startsWith("")) {
                        isInTable = false
                        /*TODO if (tableBuilder != null) {
                            builder.withContents(tableBuilder.get())
                        }*/
                    }
                    // Not code.
                    if (!line.isEmpty()) {
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

    /**
     * Convert to table line.
     * @param line
     * @return
     */
    private fun convertTableColumns(line: String?) {
            //List<TableColumn<ObservableList<String?>?, String?>>? {
        /*
        if (line == null || !line.contains("|")) {
            return Collections.emptyList()
        }
        val columnNames = line.split("\\|".toRegex()).toTypedArray()
        return IntStream.range(0, columnNames.size)
                .filter { i: Int -> !columnNames[i].isEmpty() }
                .mapToObj<Any> { i: Int -> TableColumn<ObservableList<String>, String>(columnNames[i]) }
                .collect(Collectors.toList())
         */
    }

    companion object {

        private val LINE_SEPARATOR = System.lineSeparator()

        /** Background image pattern.  */
        private val BACKGROUND: Pattern = Pattern.compile("\\!\\[background\\]\\((.+?)\\)")

        /** In-line image pattern.  */
        private val IMAGE: Pattern = Pattern.compile("\\!\\[(.?)\\]\\((.+?)\\)")

        /** CSS specifying pattern.  */
        private val CSS: Pattern = Pattern.compile("\\[css\\]\\((.+?)\\)")

        /** In-line image pattern.  */
        private val FOOTER_TEXT: Pattern = Pattern.compile("\\[footer\\]\\((.+?)\\)")
    }
}