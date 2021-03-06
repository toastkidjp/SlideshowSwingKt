package jp.toastkid.slideshow.slide.model

import jp.toastkid.slideshow.slide.view.BackgroundPanel
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JProgressBar
import kotlin.math.max
import kotlin.math.min


class SlideDeck {

    private val cardPanel = JPanel()

    private val cards = CardLayout()

    private val bottomComponents = JPanel()

    private val footerText = JLabel()

    private val progress = JProgressBar(JProgressBar.HORIZONTAL, 0, 100)

    private var background: String? = null

    init {
        cardPanel.layout = cards
        bottomComponents.layout = BoxLayout(bottomComponents, BoxLayout.Y_AXIS)
        bottomComponents.add(footerText)
        bottomComponents.add(progress)
    }

    fun add(slide: Slide) {
        //cardPanel.add(JScrollPane(slide.view()))
        cardPanel.add(slide.view())
    }

    fun first() {
        cards.first(cardPanel)
        progress.value = 1
    }

    fun last() {
        cards.last(cardPanel)
        progress.value = cardPanel.componentCount
    }

    fun back() {
        if (progress.value <= 1) {
            return
        }

        cards.previous(cardPanel)
        progress.value = max(1, progress.value - 1)
    }

    fun forward() {
        if (progress.value == cardPanel.componentCount) {
            return
        }

        cards.next(cardPanel)
        progress.value = min(cardPanel.componentCount, progress.value + 1)
    }

    fun setTo(frame: JFrame) {
        progress.maximum = cardPanel.componentCount
        progress.foreground = Color.decode("#002277")
        progress.isStringPainted = true
        progress.addChangeListener {
            progress.string = "${progress.value} / ${progress.maximum}"
        }

        val content = BackgroundPanel(ImageIO.read(Files.newInputStream(Paths.get(background))))
        content.layout = BorderLayout()
        cardPanel.isOpaque = false
        content.add(cardPanel, BorderLayout.CENTER)
        content.add(bottomComponents, BorderLayout.SOUTH)
        frame.add(content)
    }

    fun setFooterText(footerText: String?) {
        if (footerText.isNullOrBlank()) {
            return
        }
        this.footerText.text = footerText
    }

    fun setBackground(background: String) {
        this.background = background
    }

    fun containsBackground(): Boolean {
        return this.background?.isNotBlank() == true
    }

    fun generatePdf() {
        first()
        PdfGenerator().invoke(cardPanel, { forward() })
    }

}