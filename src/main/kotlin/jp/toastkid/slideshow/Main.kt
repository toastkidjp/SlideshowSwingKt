package jp.toastkid.slideshow

/**
 * Created by toastkidjp on 2020/05/09.
 *
 * @author toastkidjp
 */
fun main(args: Array<String>) {
    val frame = MainFrame("Slideshow")
    val filePath = if (args.isNullOrEmpty()) DEFAULT_FILE_NAME else args[0]
    frame.show(filePath)
}

private const val DEFAULT_FILE_NAME = "sample.md"
