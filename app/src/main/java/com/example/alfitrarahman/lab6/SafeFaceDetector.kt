package com.example.alfitrarahman.lab6

import android.graphics.ImageFormat
import android.util.Log
import android.util.SparseArray
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Frame
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by alfitrarahman on 6/3/18.
 */
class SafeFaceDetector(delegate:Detector<Face>): Detector<Face>(){
    private val TAG = "SafeFaceDetector"
    private val mDelegate: Detector<Face> = delegate

    override fun release() {
        super.release()
        mDelegate.release()
    }

    override fun detect(frame: Frame): SparseArray<Face> {
        val kMinDimension = 147
        val kDimensionLower = 640
        val width = frame.metadata.width
        val height = frame.metadata.height
        lateinit var resultFrame: Frame
        if (height > (2 * kDimensionLower)) {
            val multiple = height as Double / kDimensionLower as Double
            val lowerWidth = Math.floor(width as Double / multiple)
            if (lowerWidth < kMinDimension) {
                val newWidth = Math.ceil(kMinDimension * multiple).toInt()
                resultFrame = padFrameRight(frame, newWidth)
            }
        }
        else if (width > (2 * kDimensionLower)) {
            val multiple = width as Double / kDimensionLower as Double
            val lowerHeight = Math.floor(height as Double / multiple)
            if (lowerHeight < kMinDimension) {
                val newHeight = Math.ceil(kMinDimension * multiple).toInt()
                resultFrame = padFrameBottom(frame, newHeight)
            }
        }
        else if (width < kMinDimension) {
            resultFrame = padFrameRight(frame, kMinDimension)
        }
        return mDelegate.detect(resultFrame)
    }
    override fun isOperational():Boolean {
        return mDelegate.isOperational
    }
    override fun setFocus(id:Int):Boolean {
        return mDelegate.setFocus(id)
    }
    private fun padFrameRight(originalFrame: Frame, newWidth:Int): Frame {
        val metadata = originalFrame.metadata
        val width = metadata.width
        val height = metadata.height
        Log.i(TAG, "Padded image from: " + width + "x" + height + " to " + newWidth + "x" + height)
        val origBuffer = originalFrame.grayscaleImageData
        val origOffset = origBuffer.arrayOffset()
        val origBytes = origBuffer.array()
        val paddedBuffer = ByteBuffer.allocateDirect(newWidth * height)
        val paddedOffset = paddedBuffer.arrayOffset()
        val paddedBytes = paddedBuffer.array()
        Arrays.fill(paddedBytes, 0.toByte())
        for (y in 0 until height) {
            val origStride = origOffset + y * width
            val paddedStride = paddedOffset + y * newWidth
            System.arraycopy(origBytes, origStride, paddedBytes, paddedStride, width)
        }
        return Frame.Builder()
                .setImageData(paddedBuffer, newWidth, height, ImageFormat.NV21)
                .setId(metadata.id)
                .setRotation(metadata.rotation)
                .setTimestampMillis(metadata.timestampMillis)
                .build()
    }
    private fun padFrameBottom(originalFrame: Frame, newHeight:Int): Frame {
        val metadata = originalFrame.metadata
        val width = metadata.width
        val height = metadata.height
        Log.i(TAG, "Padded image from: " + width + "x" + height + " to " + width + "x" + newHeight)
        val origBuffer = originalFrame.grayscaleImageData
        val origOffset = origBuffer.arrayOffset()
        val origBytes = origBuffer.array()
        val paddedBuffer = ByteBuffer.allocateDirect(width * newHeight)
        val paddedOffset = paddedBuffer.arrayOffset()
        val paddedBytes = paddedBuffer.array()
        Arrays.fill(paddedBytes, 0.toByte())
        for (y in 0 until height) {
            val origStride = origOffset + y * width
            val paddedStride = paddedOffset + y * width
            System.arraycopy(origBytes, origStride, paddedBytes, paddedStride, width)
        }
        return Frame.Builder()
                .setImageData(paddedBuffer, width, newHeight, ImageFormat.NV21)
                .setId(metadata.id)
                .setRotation(metadata.rotation)
                .setTimestampMillis(metadata.timestampMillis)
                .build()
    }
}

