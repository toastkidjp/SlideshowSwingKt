package jp.toastkid.slideshow.slide.view

import java.awt.Graphics
import java.awt.Image
import java.awt.Toolkit
import javax.swing.JPanel

class BackgroundPanel(private val background: Image? = null) : JPanel() {

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (background == null) {
            return
        }

        val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
        g?.drawImage(background, 0, 0, screenSize.width, screenSize.height, null)
    }
}