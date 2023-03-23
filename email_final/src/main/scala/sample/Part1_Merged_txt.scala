import org.apache.spark.sql.SparkSession

import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source._
import scala.io.Codec
import java.nio.charset.CodingErrorAction

object Part1_Merged_txt {
  def main(args: Array[String]) {
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    val dir = "/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/maildir/"
//    val dir = "/Users/pratikdomadiya/Desktop/allen-p"
    val d = new File(dir)
    var emails_All = List.empty[String]
    var l: List[String]  = List()
    var count = 0
    if (d.exists && d.isDirectory) {
      val myFiles = d.listFiles.filter(_.isFile).toList
      val myDirs = d.listFiles().filter(_.isDirectory).toList
      for (dir <- myDirs) {
//        var f_name = "/Users/pratikdomadiya/Desktop/inbox".toString
        val f_name = dir + "/inbox".toString
        val emails = new File(f_name).listFiles.filter(_.isFile).toList
        count = count + emails.length: Int
        for (email <- emails) {
          println("#####################", email)
          println(dir)
          val lines = fromFile(email).getLines.map(_.trim) //.grouped(15).map(_.toVector)
          var temp = true
          //          val builder = new StringBuilder
          while (lines.hasNext && temp) {
            var line = lines.next()
            emails_All :+= line
            }
          emails_All :+= "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
        }
      }
    }
    else {
      println("Error Occured : ")
    }
    println("length of the email dir : "+emails_All)
    println("length of the email dir : "+emails_All.length)
    println("total elements in List : ",emails_All.count(_ == "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"))
    println("total emails founds : ",count)

    def writeFile(filename: String, lines: List[String]): Unit = {
      val file = new File(filename)
      val bw = new BufferedWriter(new FileWriter(file))
      for (line <- lines) {
        bw.write(line)
        bw.write('\n')
      }
      bw.close()
    }

    writeFile("/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/merged.txt",lines = emails_All)
  }







}