package agh.ics.gameoflife.statistics

import java.io.FileOutputStream

class CsvWriter {
    companion object {
        fun flush(fileName: String, records: List<RowData>) {
            if (records.isNotEmpty())
                FileOutputStream(fileName, true).bufferedWriter().use { writer ->
                    writer.append(
                        buildString {
                            records.forEach { this.append(it.toString()) }
                        }
                    )
                }
        }
    }
}