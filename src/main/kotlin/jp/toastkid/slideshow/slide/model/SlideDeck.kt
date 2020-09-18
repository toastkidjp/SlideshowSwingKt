package jp.toastkid.slideshow.slide.model

import java.awt.BorderLayout
import java.awt.CardLayout
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
        val content = JPanel()
        content.layout = BorderLayout()
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

    fun generatePdf() {
        first()
        PdfGenerator().invoke(cardPanel, { forward() })
    }

}