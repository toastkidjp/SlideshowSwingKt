package jp.toastkid.slideshow.slide.model

import java.awt.CardLayout
import javax.swing.JFrame
import javax.swing.JPanel

class SlideDeck {

    private val cardPanel = JPanel()

    private val cards = CardLayout()

    private var footerText: String = ""

    init {
        cardPanel.layout = cards
    }

    fun add(slide: Slide) {
        //cardPanel.add(JScrollPane(slide.view()))
        cardPanel.add(slide.view())
    }

    fun first() {
        cards.first(cardPanel)
    }

    fun back() {
        cards.previous(cardPanel)
    }

    fun forward() {
        cards.next(cardPanel)
    }

    fun setTo(frame: JFrame) {
        frame.add(cardPanel)
    }

    fun setFooterText(footerText: String?) {
        if (footerText.isNullOrBlank()) {
            return
        }
        this.footerText = footerText
    }

}