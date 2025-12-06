package com.emifuel.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.emifuel.model.CalculationResult
import com.emifuel.model.FuelType
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator {

    companion object {
        // Page dimensions (A4 format)
        private const val PAGE_WIDTH = 595  // points
        private const val PAGE_HEIGHT = 842 // points
        private const val MARGIN = 50f
        private const val LINE_SPACING = 20f
    }

    fun generateReport(result: CalculationResult, outputStream: OutputStream): Boolean {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            var yPosition = MARGIN

            // Header
            yPosition = drawHeader(canvas, yPosition)
            yPosition += LINE_SPACING

            // Section 1: Input Data
            yPosition = drawSection(canvas, "Вхідні дані", yPosition)
            yPosition = drawInputData(canvas, result, yPosition)
            yPosition += LINE_SPACING

            // Section 2: Results
            yPosition = drawSection(canvas, "Результати розрахунку", yPosition)
            yPosition = drawResults(canvas, result, yPosition)
            yPosition += LINE_SPACING

            // Section 3: Calculation Details
            if (result.formula22Details.isNotEmpty()) {
                yPosition = drawSection(canvas, "Деталі розрахунку", yPosition)
                yPosition = drawDetails(canvas, result, yPosition)
            }

            // Footer
            drawFooter(canvas, yPosition)

            pdfDocument.finishPage(page)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun drawHeader(canvas: Canvas, startY: Float): Float {
        var yPosition = startY

        // Title
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("EmiFuel - Звіт про розрахунок викидів", MARGIN, yPosition, titlePaint)
        yPosition += 25f

        // Subtitle with date
        val subtitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Дата формування: ${dateFormat.format(Date())}", MARGIN, yPosition, subtitlePaint)
        yPosition += 20f

        return yPosition
    }

    private fun drawSection(canvas: Canvas, title: String, startY: Float): Float {
        val sectionPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(title, MARGIN, startY, sectionPaint)
        return startY + LINE_SPACING
    }

    private fun drawInputData(canvas: Canvas, result: CalculationResult, startY: Float): Float {
        var yPosition = startY
        val decimalFormat = DecimalFormat("#,##0.00")

        val contentPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        // Basic parameters
        yPosition = drawBulletItem(
            canvas,
            "Технологія спалювання: ${result.inputData.combustionTechnology.displayName}",
            yPosition,
            contentPaint
        )

        yPosition = drawBulletItem(
            canvas,
            "Технологія десульфуризації: ${result.inputData.desulfurizationTechnology.displayName}",
            yPosition,
            contentPaint
        )

        yPosition = drawBulletItem(
            canvas,
            "Тип палива: ${result.inputData.fuelType.displayName}",
            yPosition,
            contentPaint
        )

        yPosition = drawBulletItem(
            canvas,
            "Витрата палива B: ${decimalFormat.format(result.inputData.fuelConsumption)} ${result.inputData.fuelType.unit}",
            yPosition,
            contentPaint
        )

        // Dust filter type (if not gas)
        if (result.inputData.fuelType != FuelType.GAS) {
            yPosition = drawBulletItem(
                canvas,
                "Тип фільтра: ${result.inputData.dustFilterType.displayName}",
                yPosition,
                contentPaint
            )
        }

        // Additional fuel parameters (if applicable)
        if (result.inputData.ashContent > 0) {
            yPosition = drawBulletItem(
                canvas,
                "Масовий вміст золи Ar: ${decimalFormat.format(result.inputData.ashContent)} %",
                yPosition,
                contentPaint
            )
        }

        if (result.inputData.lowerHeatingValue > 0) {
            yPosition = drawBulletItem(
                canvas,
                "Нижча теплота згоряння Qr: ${decimalFormat.format(result.inputData.lowerHeatingValue)} МДж/кг",
                yPosition,
                contentPaint
            )
        }

        if (result.inputData.combustiblesInAsh > 0) {
            yPosition = drawBulletItem(
                canvas,
                "Вміст горючих Гвин: ${decimalFormat.format(result.inputData.combustiblesInAsh)} %",
                yPosition,
                contentPaint
            )
        }

        if (result.ashCarryoverValue > 0) {
            yPosition = drawBulletItem(
                canvas,
                "Частка леткої золи aвин: ${decimalFormat.format(result.ashCarryoverValue)}",
                yPosition,
                contentPaint
            )
        }

        if (result.dustRemovalEfficiency > 0) {
            yPosition = drawBulletItem(
                canvas,
                "Ефективність очищення ηзу: ${decimalFormat.format(result.dustRemovalEfficiency)}",
                yPosition,
                contentPaint
            )
        }

        return yPosition
    }

    private fun drawResults(canvas: Canvas, result: CalculationResult, startY: Float): Float {
        var yPosition = startY
        val decimalFormat = DecimalFormat("#,##0.00")

        val contentPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val boldPaint = Paint().apply {
            color = Color.BLACK
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        if (result.emissionFactorBeforeClearing > 0) {
            yPosition = drawBulletItem(
                canvas,
                "Показник емісії (до очищення): ${decimalFormat.format(result.emissionFactorBeforeClearing)} г/ГДж",
                yPosition,
                contentPaint
            )
        }

        yPosition = drawBulletItem(
            canvas,
            "Показник емісії kтв (після очищення): ${decimalFormat.format(result.emissionFactor)} г/ГДж",
            yPosition,
            boldPaint
        )

        yPosition = drawBulletItem(
            canvas,
            "Валовий викид E: ${decimalFormat.format(result.totalEmission)} т",
            yPosition,
            boldPaint
        )

        return yPosition
    }

    private fun drawDetails(canvas: Canvas, result: CalculationResult, startY: Float): Float {
        var yPosition = startY

        val detailsPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        // Combine both formula details
        val fullDetails = buildString {
            if (result.formula22Details.isNotEmpty()) {
                append(result.formula22Details)
                if (result.formula21Details.isNotEmpty()) {
                    append("\n\n")
                }
            }
            if (result.formula21Details.isNotEmpty()) {
                append(result.formula21Details)
            }
        }

        // Draw details line by line with wrapping
        val lines = fullDetails.split("\n")
        for (line in lines) {
            if (yPosition > PAGE_HEIGHT - MARGIN - LINE_SPACING) {
                break // Stop if we run out of space
            }

            // Simple line wrapping
            val wrappedLines = wrapText(line, detailsPaint, PAGE_WIDTH - 2 * MARGIN)
            for (wrappedLine in wrappedLines) {
                if (yPosition > PAGE_HEIGHT - MARGIN - LINE_SPACING) {
                    break
                }
                canvas.drawText(wrappedLine, MARGIN + 20f, yPosition, detailsPaint)
                yPosition += 14f // Smaller spacing for details
            }
        }

        return yPosition
    }

    private fun drawFooter(canvas: Canvas, startY: Float) {
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 10f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val footerY = PAGE_HEIGHT - MARGIN + 10f
        canvas.drawText(
            "Згенеровано EmiFuel",
            MARGIN,
            footerY,
            footerPaint
        )
    }

    private fun drawBulletItem(
        canvas: Canvas,
        text: String,
        yPosition: Float,
        paint: Paint
    ): Float {
        canvas.drawText("•", MARGIN + 10f, yPosition, paint)
        canvas.drawText(text, MARGIN + 30f, yPosition, paint)
        return yPosition + LINE_SPACING
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)

            if (width > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }

    fun generateFileName(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HHmm", Locale.getDefault())
        return "EmiFuel_Zvit_${dateFormat.format(Date())}.pdf"
    }
}
