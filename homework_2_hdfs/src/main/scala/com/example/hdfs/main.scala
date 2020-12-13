package com.example.hdfs

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataInputStream, FileSystem, FileUtil, Path}

import java.io.BufferedInputStream
import java.net.URI

object main extends LazyLogging {
  System.setProperty("hadoop.home.dir", "/")

  val mainHDFSHost = "hdfs://namenode:9000"
  val inputDataFolder = "/satge"
  val outputDataFolder = "/ods"

  val conf = new Configuration()

  def fileSystem: FileSystem = {
    FileSystem.get(new URI(mainHDFSHost), conf)
  }

  def createFolder(folderName: String): Unit = {
    val path = new Path(folderName)
    if (!fileSystem.exists(path)) {
      fileSystem.mkdirs(path)
      logger.info(s"Create folder $folderName")
    } else {
      logger.info(s"Folder $folderName already exists")
    }
  }

  def createFile(fileName: String): Unit = {
    val path = new Path(fileName)
    if (!fileSystem.exists(path)) {
      fileSystem.createNewFile(path)
      logger.info(s"Create file $fileName")
    } else {
      logger.info(s"File $fileName already exists")
    }
  }

  def getFilesAndDirs(path: String): Array[Path] = {
    val fs = fileSystem.listStatus(new Path(path))
    FileUtil.stat2Paths(fs)
  }
  def getFileNames(path: String): Array[String] = {
    getFilesAndDirs(path)
      .filter(fileSystem.getFileStatus(_).isFile())
      .map(_.getName)
  }
  def getDirNames(path: String): Array[String] = {
    getFilesAndDirs(path)
      .filter(fileSystem.getFileStatus(_).isDirectory)
      .map(_.getName)
  }

  def openFile(fileName: String): FSDataInputStream = {
    val path = new Path(fileName)
    fileSystem.open(path)
  }

  def saveResult(files: Array[String], fromDirPath: String, toDirPath: String): Unit = {
    val fileName = files.head
    val newFilePath = s"$toDirPath/$fileName"

    createFile(newFilePath)

    val outFile = fileSystem.append(new Path(newFilePath))
    for (file <- files) {
      val currentFilePath = s"$fromDirPath/$file"
      val inFile = new BufferedInputStream(openFile(currentFilePath))
      val b = new Array[Byte](1024)
      var numBytes = inFile.read(b)
      while (numBytes > 0) {
        outFile.write(b, 0, numBytes)
        numBytes = inFile.read(b)
      }
      inFile.close()
    }
    outFile.close()
  }

  def main(args: Array[String]): Unit = {
    logger.info("Start")

    createFolder(outputDataFolder)
    getDirNames(inputDataFolder).foreach { dir =>
      logger.info(s"Prepare dir: $dir")

      val currentDirPath = s"$inputDataFolder/$dir"
      val newDirPath = s"$outputDataFolder/$dir"
      createFolder(newDirPath)

      val files = getFileNames(currentDirPath)
        .filter(!_.contains(".inprogress"))

      if (files.length > 0) {
        saveResult(files, currentDirPath, newDirPath)
      }
    }
  }
}
