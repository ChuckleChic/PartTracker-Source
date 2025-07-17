package com.example.parttracker.ui

import android.content.Context
import android.graphics.*
import android.os.*
import android.print.*
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import java.io.FileOutputStream



class BitmapPrintAdapter(private val context: Context, private val bitmap: Bitmap) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?, newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?, callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        val pdi = PrintDocumentInfo.Builder("qr_code_print.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()

        callback?.onLayoutFinished(pdi, true)
    }

    override fun onWrite(
        pages: Array<PageRange>?, destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?, callback: WriteResultCallback?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onWriteCancelled()
            return
        }

        try {
            PdfDocument().apply {
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                val page = startPage(pageInfo)
                page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                finishPage(page)

                writeTo(FileOutputStream(destination?.fileDescriptor))
                close()
            }
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: IOException) {
            callback?.onWriteFailed(e.message)
        }
    }
}
