package com.nagarseva.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.nagarseva.app.data.local.entity.ReportEntity
import com.nagarseva.app.data.local.entity.getFormattedDate
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {
    
    fun generateReportPdf(
        context: Context,
        report: ReportEntity
    ): File? {
        return try {
            // Create PDF document
            val pdfDocument = PdfDocument()
            
            // A4 page: 595 x 842 points
            val pageInfo = PdfDocument.PageInfo
                .Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            
            // ── PAINTS ──────────────────────────
            val titlePaint = Paint().apply {
                textSize = 24f
                color = android.graphics.Color
                    .parseColor("#1B5E20")
                typeface = Typeface.DEFAULT_BOLD
            }
            val headingPaint = Paint().apply {
                textSize = 16f
                color = android.graphics.Color
                    .parseColor("#212121")
                typeface = Typeface.DEFAULT_BOLD
            }
            val bodyPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color
                    .parseColor("#212121")
            }
            val greyPaint = Paint().apply {
                textSize = 11f
                color = android.graphics.Color
                    .parseColor("#757575")
            }
            val linePaint = Paint().apply {
                color = android.graphics.Color
                    .parseColor("#EEEEEE")
                strokeWidth = 1f
            }
            val greenBgPaint = Paint().apply {
                color = android.graphics.Color
                    .parseColor("#E8F5E9")
                style = Paint.Style.FILL
            }
            val greenTextPaint = Paint().apply {
                textSize = 13f
                color = android.graphics.Color
                    .parseColor("#1B5E20")
                typeface = Typeface.DEFAULT_BOLD
            }
            
            var y = 40f
            val leftMargin = 40f
            val rightEdge = 555f
            
            // ── HEADER BAR ───────────────────────
            val headerPaint = Paint().apply {
                color = android.graphics.Color
                    .parseColor("#1B5E20")
                style = Paint.Style.FILL
            }
            canvas.drawRect(
                0f, 0f, 595f, 80f, headerPaint)
            
            // App name in header
            val headerTextPaint = Paint().apply {
                textSize = 28f
                color = android.graphics.Color.WHITE
                typeface = Typeface.DEFAULT_BOLD
            }
            canvas.drawText(
                "NagarSeva", leftMargin, 52f, 
                headerTextPaint)
            
            val subHeaderPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.WHITE
                alpha = 180
            }
            canvas.drawText(
                "Your City. Your Voice. One Tap Away.",
                leftMargin, 68f, subHeaderPaint)
            
            y = 110f
            
            // ── REPORT TITLE ────────────────────
            canvas.drawText(
                "INCIDENT REPORT", 
                leftMargin, y, titlePaint)
            y += 8f
            canvas.drawLine(
                leftMargin, y, rightEdge, y, 
                linePaint)
            y += 24f
            
            // ── TICKET ID BOX ───────────────────
            canvas.drawRoundRect(
                android.graphics.RectF(
                    leftMargin, y, rightEdge, y + 50f),
                8f, 8f, greenBgPaint)
            canvas.drawText(
                "TICKET ID", 
                leftMargin + 16f, y + 16f, greyPaint)
            canvas.drawText(
                report.ticketId,
                leftMargin + 16f, y + 38f, 
                greenTextPaint)
            
            // Status on right side of box
            val statusPaint = Paint().apply {
                textSize = 12f
                color = when (report.status) {
                    "RESOLVED" -> android.graphics
                        .Color.parseColor("#2E7D32")
                    "IN_REVIEW" -> android.graphics
                        .Color.parseColor("#FF8F00")
                    else -> android.graphics
                        .Color.parseColor("#1565C0")
                }
                typeface = Typeface.DEFAULT_BOLD
            }
            canvas.drawText(
                "● ${report.status
                    .replace("_", " ")}",
                rightEdge - 100f, y + 30f, 
                statusPaint)
            
            y += 70f
            
            // ── REPORT DETAILS ──────────────────
            fun drawField(
                label: String, 
                value: String,
                currentY: Float
            ): Float {
                canvas.drawText(
                    label.uppercase(), 
                    leftMargin, currentY, greyPaint)
                canvas.drawText(
                    value, leftMargin, 
                    currentY + 18f, bodyPaint)
                canvas.drawLine(
                    leftMargin, currentY + 28f,
                    rightEdge, currentY + 28f, 
                    linePaint)
                return currentY + 44f
            }
            
            y = drawField(
                "Issue Title", 
                report.issueTitle, y)
            y = drawField(
                "Defect Type", 
                report.defectType
                    .replace("_", " ")
                    .lowercase()
                    .replaceFirstChar { 
                        it.uppercase() 
                    }, y)
            y = drawField(
                "Severity", 
                report.severity, y)
            y = drawField(
                "Location", 
                report.address, y)
            y = drawField(
                "GPS Coordinates",
                "${report.latitude}°N, " +
                "${report.longitude}°E", y)
            y = drawField(
                "Date Reported",
                report.getFormattedDate(), y)
            
            if (report.description.isNotBlank()) {
                canvas.drawText(
                    "DESCRIPTION", 
                    leftMargin, y, greyPaint)
                y += 18f
                // Word wrap description
                val words = report.description
                    .split(" ")
                var line = ""
                words.forEach { word ->
                    val testLine = 
                        if (line.isEmpty()) word 
                        else "$line $word"
                    if (bodyPaint.measureText(
                        testLine) < 
                        rightEdge - leftMargin) {
                        line = testLine
                    } else {
                        canvas.drawText(
                            line, leftMargin, y, 
                            bodyPaint)
                        y += 16f
                        line = word
                    }
                }
                if (line.isNotEmpty()) {
                    canvas.drawText(
                        line, leftMargin, y, bodyPaint)
                    y += 16f
                }
                canvas.drawLine(
                    leftMargin, y + 4f,
                    rightEdge, y + 4f, linePaint)
                y += 24f
            }
            
            // ── PHOTO ───────────────────────────
            if (report.photoPath.isNotEmpty()) {
                val photoFile = File(report.photoPath)
                if (photoFile.exists()) {
                    val bitmap = BitmapFactory
                        .decodeFile(report.photoPath)
                    if (bitmap != null) {
                        canvas.drawText(
                            "PHOTO EVIDENCE",
                            leftMargin, y, greyPaint)
                        y += 10f
                        
                        val maxWidth = 
                            rightEdge - leftMargin
                        val maxHeight = 200f
                        val scale = minOf(
                            maxWidth / bitmap.width,
                            maxHeight / bitmap.height
                        )
                        val scaledBitmap = Bitmap
                            .createScaledBitmap(
                                bitmap,
                                (bitmap.width * scale)
                                    .toInt(),
                                (bitmap.height * scale)
                                    .toInt(),
                                true
                            )
                        canvas.drawBitmap(
                            scaledBitmap, 
                            leftMargin, y, null)
                        y += scaledBitmap.height + 16f
                    }
                }
            }
            
            // ── FOOTER ──────────────────────────
            val footerY = 820f
            canvas.drawLine(
                leftMargin, footerY, 
                rightEdge, footerY, linePaint)
            canvas.drawText(
                "Generated by NagarSeva App" ,
                leftMargin, footerY + 16f, greyPaint)
            canvas.drawText(
                SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                ).format(Date()),
                rightEdge - 120f, 
                footerY + 16f, greyPaint)
            
            pdfDocument.finishPage(page)
            
            // Save to cache directory
            val pdfDir = File(
                context.cacheDir, "reports")
            pdfDir.mkdirs()
            val pdfFile = File(
                pdfDir, 
                "NagarSeva_${report.ticketId}.pdf"
            )
            
            FileOutputStream(pdfFile).use { 
                outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            
            pdfFile
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun sharePdf(context: Context, pdfFile: File) {
        val pdfUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        
        val shareIntent = Intent(
            Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(
                Intent.EXTRA_SUBJECT,
                "NagarSeva Incident Report"
            )
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(
            Intent.createChooser(
                shareIntent, 
                "Share Report PDF"
            )
        )
    }
    
    fun openPdf(context: Context, pdfFile: File) {
        val pdfUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        
        val openIntent = Intent(
            Intent.ACTION_VIEW).apply {
            setDataAndType(pdfUri, "application/pdf")
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        try {
            context.startActivity(openIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No PDF viewer found. " +
                "Please install a PDF app.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
