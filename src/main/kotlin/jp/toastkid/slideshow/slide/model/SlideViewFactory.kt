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

class SlideViewFactory {

    operator fun invoke(lines: Collection<JComponent>, backgroundPath: String, front: Boolean, title: String): JPanel {
        val titleLabel = JLabel("<html>${title}</html>", SwingConstants.CENTER)
        titleLabel.font = titleLabel.font.deriveFont(if (front) 180f else 120.0f)

        val background =
                if (backgroundPath.isNotBlank()) ImageIO.read(Files.newInputStream(Paths.get(backgroundPath))) else null
        val panel = BackgroundPanel(background)
        panel.isOpaque = background != null
        if (front) {
            panel.layout = BoxLayout(panel, BoxLayout.LINE_AXIS)
            val centeringPanel = JPanel()
            centeringPanel.layout = BoxLayout(centeringPanel, BoxLayout.PAGE_AXIS)
            centeringPanel.add(titleLabel)
            centeringPanel.alignmentY = JComponent.CENTER_ALIGNMENT
            lines.forEach { centeringPanel.add(it) }
            panel.add(centeringPanel)
        } else {
            panel.layout = BoxLayout(panel, BoxLayout.PAGE_AXIS)
            panel.add(titleLabel)
            lines.forEach { panel.add(it) }
        }
        return panel
    }
}