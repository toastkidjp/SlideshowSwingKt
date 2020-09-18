package jp.toastkid.slideshow.slide.model

import com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
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
        val start = System.currentTimeMillis()
        LOGGER.info("Start generating PDF.")
        try {
            PDDocument().use({ doc ->
                first()
                repeat(cardPanel.componentCount) { i: Int ->
                    val istart = System.currentTimeMillis()
                    val page = PDPage(PDRectangle(cardPanel.width.toFloat(), cardPanel.height.toFloat()))
                    try {
                        PDPageContentStream(doc, page).use({ content ->
                            val screenshot = BufferedImage(cardPanel.getSize().width, cardPanel.getSize().height, BufferedImage.TYPE_INT_RGB)
                            cardPanel.paint(screenshot.createGraphics())
                            content.drawImage(
                                    LosslessFactory.createFromImage(doc, screenshot),
                                    0f,
                                    0f
                            )
                        })
                    } catch (ie: IOException) {
                        ie.printStackTrace()
                    }
                    doc.addPage(page)
                    println("Ended page $i. ${System.currentTimeMillis() - istart}[ms]")
                    forward()
                }
                doc.save(File("slide.pdf"))
                //snackbar.fireEvent(SnackbarEvent("Ended generating PDF."))
            })
        } catch (ie: IOException) {
            ie.printStackTrace()
        }
        println("Ended generating PDF. ${System.currentTimeMillis() - start}[ms]")
    }

}