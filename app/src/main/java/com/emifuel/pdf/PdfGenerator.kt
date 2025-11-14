package com.emifuel.pdf

import android.content.Context
import android.os.Environment
import com.emifuel.model.CalculationResult
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    fun generatePdf(result: CalculationResult): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(downloadsDir, "EmiFuel_Report_$timestamp.pdf")

            val writer = PdfWriter(file)
            val pdfDoc = PdfDocument(writer)
            val document = Document(pdfDoc)

            val decimalFormat = DecimalFormat("#,##0.00")

            // 03>;>2>:
            document.add(
                Paragraph("EmiFuel - Zvit pro rozrakhunok vykydiv")
                    .setFontSize(18f)
                    .setBold()
            )

            document.add(Paragraph("Data: ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())}"))
            document.add(Paragraph("\n"))

            // "01;8FO 2EV4=8E 40=8E
            document.add(Paragraph("Vkhidni dani").setFontSize(14f).setBold())
            val inputTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                .useAllAvailableWidth()

            inputTable.addCell("Tekhnolohiia spaluvannia")
            inputTable.addCell(result.inputData.combustionTechnology)

            inputTable.addCell("Tekhnolohiia desulfuryzatsii")
            inputTable.addCell(result.inputData.desulfurizationTechnology)

            inputTable.addCell("Typ palyva")
            inputTable.addCell(result.inputData.fuelType.displayName)

            inputTable.addCell("Vytrata palyva")
            inputTable.addCell("${decimalFormat.format(result.inputData.fuelConsumption)} ${result.inputData.fuelType.unit}")

            document.add(inputTable)
            document.add(Paragraph("\n"))

            // "01;8FO @57C;LB0BV2
            document.add(Paragraph("Rezultaty rozrakhunku").setFontSize(14f).setBold())
            val resultsTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                .useAllAvailableWidth()

            resultsTable.addCell("Pokaznyk emisii")
            resultsTable.addCell("${decimalFormat.format(result.emissionFactor)} g/GDzh")

            resultsTable.addCell("Valovyi vykyd")
            resultsTable.addCell("${decimalFormat.format(result.totalEmission)} t")

            document.add(resultsTable)

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
