package jp.toastkid.slideshow

/**
 * Created by toastkidjp on 2020/05/09.
 *
 * @author toastkidjp
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        val frame = MainFrame("Slideshow")
        val filePath = if (args.isNullOrEmpty()) "sample.md" else args[0]
        frame.show(filePath)
    }

}
