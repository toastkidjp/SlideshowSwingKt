package jp.toastkid.slideshow.slide.model

import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class Slide {

    private var title = ""

    private var backgroundPath = ""

    private var front = false

    private val lines = mutableListOf<JComponent>()

    fun setTitle(title: String) {
        this.title = title
    }

    fun hasTitle() = this.title.isNotBlank()

    fun addText(line: String) {
        val lineLabel = JLabel("<html>$line</html>")
        lineLabel.font = lineLabel.font.deriveFont(72.0f)
        this.lines.add(lineLabel)
    }

    fun add(line: JComponent) {
        this.lines.add(line)
    }

    fun setBackground(background: String) {
        backgroundPath = background
    }

    fun isFront(front: Boolean) {
        this.front = front
    }

    fun view(): JPanel {
        return SlideViewFactory().invoke(lines, backgroundPath, front, title)
    }
}