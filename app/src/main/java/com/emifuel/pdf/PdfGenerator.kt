package com.emifuel.pdf

import android.content.Context
import android.os.Environment
import com.emifuel.model.CalculationResult
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
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

    // Транслітерація українського тексту в латиницю для PDF
    private fun transliterate(text: String): String {
        val map = mapOf(
            'А' to "A", 'Б' to "B", 'В' to "V", 'Г' to "H", 'Ґ' to "G", 'Д' to "D", 'Е' to "E",
            'Є' to "Ye", 'Ж' to "Zh", 'З' to "Z", 'И' to "Y", 'І' to "I", 'Ї' to "Yi", 'Й' to "Y",
            'К' to "K", 'Л' to "L", 'М' to "M", 'Н' to "N", 'О' to "O", 'П' to "P", 'Р' to "R",
            'С' to "S", 'Т' to "T", 'У' to "U", 'Ф' to "F", 'Х' to "Kh", 'Ц' to "Ts", 'Ч' to "Ch",
            'Ш' to "Sh", 'Щ' to "Shch", 'Ь' to "", 'Ю' to "Yu", 'Я' to "Ya",
            'а' to "a", 'б' to "b", 'в' to "v", 'г' to "h", 'ґ' to "g", 'д' to "d", 'е' to "e",
            'є' to "ie", 'ж' to "zh", 'з' to "z", 'и' to "y", 'і' to "i", 'ї' to "i", 'й' to "i",
            'к' to "k", 'л' to "l", 'м' to "m", 'н' to "n", 'о' to "o", 'п' to "p", 'р' to "r",
            'с' to "s", 'т' to "t", 'у' to "u", 'ф' to "f", 'х' to "kh", 'ц' to "ts", 'ч' to "ch",
            'ш' to "sh", 'щ' to "shch", 'ь' to "", 'ю' to "iu", 'я' to "ia"
        )
        return buildString {
            for (char in text) {
                append(map[char] ?: char.toString())
            }
        }
    }

    fun generatePdf(result: CalculationResult): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(downloadsDir, "EmiFuel_Report_$timestamp.pdf")

            val writer = PdfWriter(file)
            val pdfDoc = PdfDocument(writer)
            val document = Document(pdfDoc)

            val decimalFormat = DecimalFormat("#,##0.00")

            // Заголовок
            document.add(
                Paragraph(transliterate("EmiFuel - Звіт про розрахунок викидів"))
                    .setFontSize(18f)
                    .setBold()
            )

            document.add(Paragraph(transliterate("Дата: ") + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())))
            document.add(Paragraph("\n"))

            // Вхідні дані
            document.add(Paragraph(transliterate("Вхідні дані")).setFontSize(14f).setBold())
            val inputTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                .useAllAvailableWidth()

            inputTable.addCell(transliterate("Технологія спалювання"))
            inputTable.addCell(transliterate(result.inputData.combustionTechnology.displayName))

            inputTable.addCell(transliterate("Технологія десульфуризації"))
            inputTable.addCell(transliterate(result.inputData.desulfurizationTechnology.displayName))

            inputTable.addCell(transliterate("Тип палива"))
            inputTable.addCell(transliterate(result.inputData.fuelType.displayName))

            inputTable.addCell(transliterate("Витрата палива B"))
            inputTable.addCell("${decimalFormat.format(result.inputData.fuelConsumption)} ${transliterate(result.inputData.fuelType.unit)}")

            // Додаткові параметри палива (якщо не газ)
            if (result.inputData.ashContent > 0) {
                inputTable.addCell(transliterate("Масовий вміст золи Ar"))
                inputTable.addCell("${decimalFormat.format(result.inputData.ashContent)} %")
            }

            if (result.inputData.lowerHeatingValue > 0) {
                inputTable.addCell(transliterate("Нижча теплота згоряння Qr"))
                inputTable.addCell(transliterate("${decimalFormat.format(result.inputData.lowerHeatingValue)} МДж/кг"))
            }

            if (result.inputData.combustiblesInAsh > 0) {
                inputTable.addCell(transliterate("Вміст горючих Гвин"))
                inputTable.addCell("${decimalFormat.format(result.inputData.combustiblesInAsh)} %")
            }

            if (result.inputData.sulfurContent > 0) {
                inputTable.addCell(transliterate("Масовий вміст сірки Sr"))
                inputTable.addCell("${decimalFormat.format(result.inputData.sulfurContent)} %")
            }

            if (result.ashCarryoverValue > 0) {
                inputTable.addCell(transliterate("Частка леткої золи aвин"))
                inputTable.addCell(decimalFormat.format(result.ashCarryoverValue))
            }

            if (result.dustRemovalEfficiency > 0) {
                inputTable.addCell(transliterate("Ефективність очищення ηзу"))
                inputTable.addCell(decimalFormat.format(result.dustRemovalEfficiency))
            }

            document.add(inputTable)
            document.add(Paragraph("\n"))

            // Результати
            document.add(Paragraph(transliterate("Результати розрахунку")).setFontSize(14f).setBold())
            val resultsTable = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
                .useAllAvailableWidth()

            if (result.emissionFactorBeforeClearing > 0) {
                resultsTable.addCell(transliterate("Показник емісії (до очищення)"))
                resultsTable.addCell(transliterate("${decimalFormat.format(result.emissionFactorBeforeClearing)} г/ГДж"))
            }

            resultsTable.addCell(transliterate("Показник емісії kтв (після очищення)"))
            resultsTable.addCell(transliterate("${decimalFormat.format(result.emissionFactor)} г/ГДж"))

            resultsTable.addCell(transliterate("Валовий викид E"))
            resultsTable.addCell(transliterate("${decimalFormat.format(result.totalEmission)} т"))

            document.add(resultsTable)
            document.add(Paragraph("\n"))

            // Деталі розрахунку формули (2.2)
            if (result.formula22Details.isNotEmpty()) {
                document.add(Paragraph(transliterate("Деталі розрахунку формули (2.2)")).setFontSize(12f).setBold())
                document.add(Paragraph(transliterate(result.formula22Details)).setFontSize(10f))
                document.add(Paragraph("\n"))
            }

            // Деталі розрахунку формули (2.1)
            if (result.formula21Details.isNotEmpty()) {
                document.add(Paragraph(transliterate("Деталі розрахунку формули (2.1)")).setFontSize(12f).setBold())
                document.add(Paragraph(transliterate(result.formula21Details)).setFontSize(10f))
            }

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
