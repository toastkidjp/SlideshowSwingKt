package jp.toastkid.slideshow.slide.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class BackgroundExtractorTest {

    private val extractor = BackgroundExtractor()

    @Test
    fun test() {
        assertNull(extractor.invoke("![](https://www.yahoo.co.jp)"))
        assertEquals("https://www.yahoo.co.jp", extractor.invoke("![background](https://www.yahoo.co.jp)"))
    }

}