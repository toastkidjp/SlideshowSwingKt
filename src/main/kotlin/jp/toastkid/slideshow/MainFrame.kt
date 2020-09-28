package jp.toastkid.slideshow

import jp.toastkid.slideshow.slide.model.SlideDeck
import jp.toastkid.slideshow.slide.parser.SlideDeckReader
import java.awt.GraphicsEnvironment
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.nio.file.Paths
import javax.swing.JFrame
import kotlin.system.exitProcess

class MainFrame(private val title: String) {

    private val device = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice

    private val frame = JFrame(title)

    private var deck: SlideDeck? = null

    fun show(filePath: String) {
        deck = SlideDeckReader(Paths.get(filePath)).invoke()

        deck?.setTo(frame)

        frame.setBounds(200, 200, 800, 600)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        frame.addKeyListener(object : KeyAdapter() {

            override fun keyReleased(e: KeyEvent?) {
                when (e?.keyCode) {
                    KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER -> {
                        if (e.isControlDown) deck?.last() else deck?.forward()
                    }
                    KeyEvent.VK_LEFT, KeyEvent.VK_BACK_SPACE -> {
                        if (e.isControlDown) deck?.first() else deck?.back()
                    }
                    KeyEvent.VK_F5 -> fullScreen()
                    KeyEvent.VK_ESCAPE -> window()
                    KeyEvent.VK_P -> if (e.isControlDown) { deck?.generatePdf() }
                }
            }

        })

        frame.isUndecorated = true
        fullScreen()
        frame.isVisible = true
        deck?.first()
    }

    private fun fullScreen() {
        if (device.fullScreenWindow != null) {
            window()
            return
        }
        device.fullScreenWindow = frame
    }

    private fun window() {
        if (device.fullScreenWindow == null) {
            exitProcess(0)
        }
        device.fullScreenWindow = null
    }

}