package jp.toastkid.slideshow.slide.parser

import javax.swing.BoxLayout
import javax.swing.DefaultCellEditor
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class TableBuilder {

    private var table: JTable? = null

    fun hasColumns(): Boolean = table != null

    fun setColumns(line: String) {
        val columnNames = line.split("|").drop(1).toTypedArray()
        val tableModel = DefaultTableModel(columnNames, 0)
        table = JTable(tableModel)
        (table?.getDefaultEditor(Any::class.java) as? DefaultCellEditor)?.component?.font?.deriveFont(30f)
        table?.isFocusable = false
        table?.isEnabled = false
    }

    fun addTableLines(line: String) {
        line.split("|").drop(1).also {
            (table?.model as? DefaultTableModel)?.addRow(it.toTypedArray())
        }
    }

    fun get(): JComponent? = JPanel().also {
        it.layout = BoxLayout(it, BoxLayout.PAGE_AXIS)
        it.add(table?.tableHeader)
        it.add(table)
    }

    companion object {

        fun isTableStart(line: String) = line.startsWith("|")
    }
}