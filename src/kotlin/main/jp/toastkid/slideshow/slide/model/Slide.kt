package jp.toastkid.slideshow.slide.model

import jp.toastkid.slideshow.slide.view.BackgroundPanel
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class Slide {

    private var title = ""

    private var backgroundPath = ""

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

    fun view(): JPanel {
        val titleLabel = JLabel("<html>${this.title}</html>", SwingConstants.CENTER)
        titleLabel.font = titleLabel.font.deriveFont(120.0f)

        val background =
                if (backgroundPath.isNotBlank()) ImageIO.read(Files.newInputStream(Paths.get(backgroundPath))) else null
        val panel = BackgroundPanel(background)
        panel.isOpaque = background != null
        panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
        panel.add(titleLabel)
        lines.forEach { panel.add(it) }
        return panel
    }
}