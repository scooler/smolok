/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.smolok.lib.download

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.Validate
import smolok.lib.process.ProcessManager

import java.util.zip.ZipInputStream

import static org.apache.commons.io.IOUtils.copyLarge
import static org.slf4j.LoggerFactory.getLogger
import static smolok.lib.process.ExecutorBasedProcessManager.command

/**
 * Downloads and caches binary files.
 */
class DownloadManager {

    // Logger

    private final static LOG = getLogger(DownloadManager.class)

    // Members

    private final ProcessManager processManager

    private final File downloadDirectory

    // Constructors

    DownloadManager(ProcessManager processManager, File downloadDirectory) {
        this.processManager = processManager
        this.downloadDirectory = downloadDirectory

        downloadDirectory.mkdirs()
    }

    // Download operations

    void download(BinaryCoordinates image) {
        Validate.notNull(image.source(), 'Source URL cannot be null.')
        Validate.notNull(image.fileName(), 'Please indicate the name of the target file.')

        def targetFile = downloadedFile(image.fileName)
        if(!targetFile.exists()) {
            LOG.debug('File {} does not exist - downloading...', targetFile.absolutePath)
            def tmpFile = File.createTempFile('smolok', 'download')
            try {
                copyLarge(image.source().openStream(), new FileOutputStream(tmpFile))
            } catch (UnknownHostException e) {
                throw new FileDownloadException(targetFile.name, e)
            }
            targetFile.parentFile.mkdirs()
            tmpFile.renameTo(targetFile)
            LOG.debug('Saved downloaded file to {}.', targetFile.absolutePath)

            if(image.extractedFileName != null) {
                def extractedImage = downloadedFile(image.extractedFileName)
                if (!extractedImage.exists()) {
                    if(targetFile.name.endsWith('.zip')) {
                        def zip = new ZipInputStream(new FileInputStream(targetFile))
                        zip.nextEntry
                        IOUtils.copyLarge(zip, new FileOutputStream(extractedImage))
                        zip.close()
                    } else if(targetFile.name.endsWith('.tar.gz')) {
                        extractedImage.mkdirs()
                        processManager.execute(command("tar xvpf ${targetFile} -C ${extractedImage}"))
                    } else {
                        throw new UnsupportedCompressionFormatException(targetFile.name)
                    }
                }
            }
        } else {
            LOG.debug('File {} exists - download skipped.', targetFile)
        }
    }

    // File access operations

    File downloadDirectory() {
        downloadDirectory
    }

    File downloadedFile(String name) {
        new File(downloadDirectory, name)
    }

    static class BinaryCoordinates {

        private final URL source

        private final String fileName

        private final String extractedFileName

        BinaryCoordinates(URL source, String fileName, String extractedFileName) {
            this.source = source
            this.fileName = fileName
            this.extractedFileName = extractedFileName
        }

        BinaryCoordinates(URL source, String fileName) {
            this(source, fileName, null)
        }


        URL source() {
            source
        }

        String fileName() {
            fileName
        }

        String extractedFileName() {
            extractedFileName
        }

    }

}