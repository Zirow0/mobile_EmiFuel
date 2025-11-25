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

            // Вхідні дані
            document.add(Paragraph("Vkhidni dani").setFontSize(14f).setBold())
            val inputTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                .useAllAvailableWidth()

            inputTable.addCell("Tekhnolohiia spaluvannia")
            inputTable.addCell(result.inputData.combustionTechnology.displayName)

            inputTable.addCell("Tekhnolohiia desulfuryzatsii")
            inputTable.addCell(result.inputData.desulfurizationTechnology.displayName)

            inputTable.addCell("Typ palyva")
            inputTable.addCell(result.inputData.fuelType.displayName)

            inputTable.addCell("Vytrata palyva B")
            inputTable.addCell("${decimalFormat.format(result.inputData.fuelConsumption)} ${result.inputData.fuelType.unit}")

            // Додаткові параметри палива (якщо не газ)
            if (result.inputData.ashContent > 0) {
                inputTable.addCell("Masovyi vmist zoly Ar")
                inputTable.addCell("${decimalFormat.format(result.inputData.ashContent)} %")
            }

            if (result.inputData.lowerHeatingValue > 0) {
                inputTable.addCell("Nyzhcha teplota zhoriannia Qr")
                inputTable.addCell("${decimalFormat.format(result.inputData.lowerHeatingValue)} MDzh/kg")
            }

            if (result.inputData.combustiblesInAsh > 0) {
                inputTable.addCell("Vmist horiuchykh Hvyn")
                inputTable.addCell("${decimalFormat.format(result.inputData.combustiblesInAsh)} %")
            }

            if (result.inputData.sulfurContent > 0) {
                inputTable.addCell("Masovyi vmist sirky Sr")
                inputTable.addCell("${decimalFormat.format(result.inputData.sulfurContent)} %")
            }

            if (result.ashCarryoverValue > 0) {
                inputTable.addCell("Chastka letkoi zoly avyn")
                inputTable.addCell(decimalFormat.format(result.ashCarryoverValue))
            }

            if (result.dustRemovalEfficiency > 0) {
                inputTable.addCell("Efektyvnist ochyshchennia etazu")
                inputTable.addCell(decimalFormat.format(result.dustRemovalEfficiency))
            }

            document.add(inputTable)
            document.add(Paragraph("\n"))

            // Результати
            document.add(Paragraph("Rezultaty rozrakhunku").setFontSize(14f).setBold())
            val resultsTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                .useAllAvailableWidth()

            if (result.emissionFactorBeforeClearing > 0) {
                resultsTable.addCell("Pokaznyk emisii (do ochyshchennia)")
                resultsTable.addCell("${decimalFormat.format(result.emissionFactorBeforeClearing)} g/GDzh")
            }

            resultsTable.addCell("Pokaznyk emisii ktv (pislia ochyshchennia)")
            resultsTable.addCell("${decimalFormat.format(result.emissionFactor)} g/GDzh")

            resultsTable.addCell("Valovyi vykyd E")
            resultsTable.addCell("${decimalFormat.format(result.totalEmission)} t")

            document.add(resultsTable)
            document.add(Paragraph("\n"))

            // Деталі розрахунку формули (2.2)
            if (result.formula22Details.isNotEmpty()) {
                document.add(Paragraph("Detali rozrakhunku formuly (2.2)").setFontSize(12f).setBold())
                document.add(Paragraph(result.formula22Details).setFontSize(10f))
                document.add(Paragraph("\n"))
            }

            // Деталі розрахунку формули (2.1)
            if (result.formula21Details.isNotEmpty()) {
                document.add(Paragraph("Detali rozrakhunku formuly (2.1)").setFontSize(12f).setBold())
                document.add(Paragraph(result.formula21Details).setFontSize(10f))
            }

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
