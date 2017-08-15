package com.kirakishou.fileserver.fixmypc.util

import net.coobird.thumbnailator.Thumbnails
import java.awt.Dimension
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object ImageUtils {

    @Throws(IOException::class)
    fun resizeAndSaveImageOnDisk(file: File, newMaxSize: Dimension, sizeType: String, currentFolderDirPath: String, imageNewName: String, extension: String) {
        val imageToResize = ImageIO.read(file)
        val outputResizedFile = File(currentFolderDirPath + imageNewName + sizeType + '.' + extension)

        //original image size should be bigger than the new size, otherwise we don't need to resize image, just copy it
        if (imageToResize.width > newMaxSize.width || imageToResize.height > newMaxSize.height) {
            val resizedImage = Thumbnails.of(imageToResize)
                    .useExifOrientation(true)
                    .size(newMaxSize.width, newMaxSize.height)
                    .asBufferedImage()

            ImageIO.write(resizedImage, extension, outputResizedFile)

        } else {
            file.copyTo(outputResizedFile)
        }
    }
}