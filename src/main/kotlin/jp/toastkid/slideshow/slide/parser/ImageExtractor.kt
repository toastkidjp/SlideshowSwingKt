package jp.toastkid.slideshow.slide.parser

import java.util.ArrayList
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ImageExtractor {

    operator fun invoke(line: String?): JComponent {
        val imagePanel = JPanel().also { panel ->
            panel.layout = BoxLayout(panel, BoxLayout.LINE_AXIS)
            panel.alignmentX = JComponent.CENTER_ALIGNMENT
        }

        if (line.isNullOrBlank()) {
            return imagePanel
        }

        extractImageUrls(line)
                .filterNotNull()
                .map {
                    JLabel(ImageIcon(it))
                }
                .forEach {
                    it.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    imagePanel.add(it)
                }
        return imagePanel
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

        /** In-line image pattern.  */
        private val IMAGE: Pattern = Pattern.compile("\\!\\[(.+?)\\]\\((.+?)\\)")

    }
}