import org.apache.spark.sql.SparkSession

import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source._
import scala.io.Codec
import scala.io.Codec

import java.nio.charset.CodingErrorAction

object Part1_1_merged_head {
  def main(args: Array[String]) {
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    val dir = "/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/maildir/"
    val d = new File(dir)
    var emails_All = List.empty[String]
    var l: List[String]  = List()
    var count = 0
    if (d.exists && d.isDirectory) {
      val myFiles = d.listFiles.filter(_.isFile).toList
      val myDirs = d.listFiles().filter(_.isDirectory).toList
      for(dir <- myDirs){
        val f_name = (dir+"/"+"inbox").toString
        val emails = new File(f_name).listFiles.filter(_.isFile).toList
        count=count+emails.length:Int
        for(email <- emails){
          println("#####################",email)
          println(dir)
          val lines = fromFile(email).getLines.map(_.trim)//.grouped(15).map(_.toVector)
          var temp=true
//          val builder = new StringBuilder
          while (lines.hasNext && temp){
            var line = lines.next()
            if (line.startsWith("X-bcc:")){
//              builder ++= "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
              emails_All :+=line
              emails_All :+="$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"
                temp=false
            }else{
//            builder ++= line
            emails_All :+=line
            }
          }
          var Message_ID = ""
          var Date = ""
          var From = ""
          var To = ""
          var Subject = ""
          var Mime_Version = ""
          var Content_Type = ""
          var Content_Transfer_Encoding = ""
          var X_From = ""
          var X_To = ""
          var X_cc = ""
          var X_bcc = ""
          for (x <- lines) {
            if (x.startsWith("Message-ID:")) {
//              Message_ID = x.split("Message-ID:")(1).trim
              println("Messa id found : ")
              l = l :+ x
            } else if (x.startsWith("Date:")) {
//              Date = x.split("Date:")(1).trim
              println("Date  found : ")
              l = l :+ x
            } else if (x.startsWith("From:")) {
//              From = x.split("From:")(1)
              println("From found : ")
              l = l :+ x
            } else if (x.startsWith("To:")) {
//              To = x.split("To:")(1)
              println("To found : ")
              l = l :+ x
            } else if (x.startsWith("Subject:")) {
//              Subject = x.split("Subject:")(1).trim
              println("Subject: found : ")
              l = l :+ x
            } else if (x.startsWith("Mime-Version:")) {
//              Mime_Version = x.split("Mime-Version:")(1)
              println("Mime-Version found : ")
              l = l :+ x
            } else if (x.startsWith("Content-Type:")) {
//              Content_Type = x.split("Content-Type:")(1)
              println("Content-Type found : ")
              l = l :+ x
            } else if (x.startsWith("Content-Transfer-Encoding:")) {
//              Content_Transfer_Encoding = x.split("Content-Transfer-Encoding:")(1).trim
              println("Content-Transfer-Encoding found : ")
              l = l :+ x
            } else if (x.startsWith("X-From:")) {
//              X_From = x.split("X-From:")(1)
              println("X-From found : ")
              l = l :+ x
            } else if (x.startsWith("X-To:")) {
//              X_To = x.split("X-To:")(1)
              println("X-To found : ")
              l = l :+ x
            } else if (x.startsWith("X-cc:")) {
//              X_cc = x.split("X-cc:")(1)
              println("X-cc  found : ")
              l = l :+ x
            } else if (x.startsWith("X-bcc:")) {
//              X_bcc = x.split("X-bcc:")(1)
              println("X-bcc found : ")
              l = l :+ x

            }
          }
          l= l :+ "###########################################"
        }
      }
    } else {
      println("error occured : ")
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

    writeFile("/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/merged_head.txt",lines = emails_All)
  }





}