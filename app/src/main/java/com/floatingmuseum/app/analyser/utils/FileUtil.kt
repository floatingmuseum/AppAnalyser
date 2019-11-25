package com.floatingmuseum.app.analyser.utils

import android.net.Uri
import android.os.Environment
import android.util.Log
import com.floatingmuseum.app.analyser.App
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.RoundingMode
import java.text.NumberFormat

/**
 * Created by Floatingmuseum on 2019-10-30.
 */

/* Checks if external storage is available for read and write */
fun isExternalStorageWritable(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

/* Checks if external storage is available to at least read */
fun isExternalStorageReadable(): Boolean {
    return Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
}

//fun copyFile(sourcePath: String?, destPath: String?): Boolean {
//    val isSuccess = false
//    sourcePath?.let { source ->
//        destPath?.let { dest ->
//            if (source.isNotBlank() && dest.isNotBlank()) {
//                val sourceFile = File(source)
//                val destFile = File(dest)
//                if (sourceFile.exists()) {
//
//                } else {
//
//                }
//            }
//        }
//    }
//    return isSuccess
//}

fun copyFile(sourcePath: String, destPath: String, bufferSize: Int = DEFAULT_BUFFER_SIZE) {
    val sourceFile = File(sourcePath)
    val contentResolver = App.context.contentResolver
    val fileDescriptor = contentResolver.openFileDescriptor(Uri.parse(destPath), "w")
    fileDescriptor?.let {
        sourceFile.copyTo(File(destPath), true)
        val outputStream = FileOutputStream(it.fileDescriptor)
        val inputStream = FileInputStream(sourceFile)
        inputStream.bufferedReader().read()
        val buffer = ByteArray(bufferSize)
        val readLength: Long = 0
        while (inputStream.read(buffer) != -1) {
            outputStream.write(buffer)
        }
//        while ((val byteCount = inputStream.read(buffer)) != -1) {
//        outputStream.write(buffer, 0, byteCount);
//        }
//        outputStream.flush()
    }
}

fun copyFile(sourcePath: String, targetUri: Uri): Boolean {
    var copySuccess = false
    val parcelFileDescriptor = App.context.contentResolver.openFileDescriptor(targetUri, "w")
    parcelFileDescriptor?.let {
        val fileOutputStream = FileOutputStream(parcelFileDescriptor.fileDescriptor)
        val sourceFile = File(sourcePath)
        Log.d("FileUtil", "copyFile()...sourceFile:${sourceFile.length()}")

        val transferredLength = sourceFile.inputStream().channel.transferTo(
            0,
            sourceFile.length(),
            fileOutputStream.channel
        )
        copySuccess = sourceFile.length() == transferredLength
        Log.d("FileUtil", "copyFile()...transferredLength:$transferredLength")

    }
    return copySuccess
}

fun formatFileSize(
    length: Long,
    roundMode: RoundingMode = RoundingMode.DOWN,
    decimalLength: Int = 1
): String {
    val lengthF = length.toFloat()
    val numberFormat = NumberFormat.getNumberInstance()
    numberFormat.maximumFractionDigits = decimalLength
    numberFormat.roundingMode = roundMode
    return when {
        length >= 1073741824 -> {
            "${numberFormat.format(lengthF / 1073741824)}gb"
        }
        length >= 1048576 -> "${numberFormat.format(lengthF / 1048576)}mb"
        length >= 1024 -> "${numberFormat.format(lengthF / 1024)}kb"
        else -> "${length}byte"
    }
}


//private static boolean SaveFile(Context context, File file, Uri uri) {
//    if (uri == null) {
//        LogUtil.e("url is null");
//        return false;
//    }
//    LogUtil.i("SaveFile: " + file.getName());
//    ContentResolver contentResolver = context . getContentResolver ();
//
//    ParcelFileDescriptor parcelFileDescriptor = null;
//    try {
//        parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "w");
//    } catch (FileNotFoundException e) {
//        e.printStackTrace();
//    }
//
//    if (parcelFileDescriptor == null) {
//        LogUtil.e("parcelFileDescriptor is null");
//        return false;
//    }
//
//    FileOutputStream outputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
//    FileInputStream inputStream;
//    try {
//        inputStream = new FileInputStream (file);
//    } catch (FileNotFoundException e) {
//        LogUtil.e(e.toString());
//        try {
//            outputStream.close();
//        } catch (IOException ex) {
//            LogUtil.e(ex.toString());
//        }
//        return false;
//    }
//
//    try {
//        copy(inputStream, outputStream);
//    } catch (IOException e) {
//        LogUtil.e(e.toString());
//        return false;
//    } finally {
//        try {
//            outputStream.close();
//        } catch (IOException e) {
//            LogUtil.e(e.toString());
//        }
//        try {
//            inputStream.close();
//        } catch (IOException e) {
//            LogUtil.e(e.toString());
//        }
//    }
//
//    return true;
//}
//
////注意当文件比较大时该方法耗时会比较大
//private static void copy(InputStream ist, OutputStream ost) throws IOException {
//    byte[] buffer = new byte[4096];
//    int byteCount;
//    while ((byteCount = ist.read(buffer)) != -1) {
//        ost.write(buffer, 0, byteCount);
//    }
//    ost.flush();
//}