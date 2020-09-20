package jp.toastkid.slideshow.slide.model

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.swing.JPanel

class PdfGenerator {

    operator fun invoke(cardPanel: JPanel, forward: () -> Unit) {
        val start = System.currentTimeMillis()
        println("Start generating PDF.")
        try {
            PDDocument().use { doc ->
                repeat(cardPanel.componentCount) { i: Int ->
                    val istart = System.currentTimeMillis()
                    val page = PDPage(PDRectangle(cardPanel.width.toFloat(), cardPanel.height.toFloat()))
                    writeToPage(doc, page, cardPanel)
                    doc.addPage(page)
                    println("Ended page $i. ${System.currentTimeMillis() - istart}[ms]")
                    forward()
                }
                doc.save(File("slide.pdf"))
            }
        } catch (ie: IOException) {
            ie.printStackTrace()
        }
        println("Ended generating PDF. ${System.currentTimeMillis() - start}[ms]")
    }

    private fun writeToPage(doc: PDDocument, page: PDPage, cardPanel: JPanel) {
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
    }

}