package sample
import org.apache.spark.sql.SparkSession

import java.io.File
object spark_read_files extends App {
  val spark = SparkSession.builder()
        .master("local[*]")
        .appName("readFilesExample")
        .getOrCreate()

  val dir = "/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/maildir/"
//  val d = new File(dir)
//  if (d.exists && d.isDirectory) {
//    val myFiles = d.listFiles.filter(_.isFile).toList
//    val myDirs = d.listFiles().filter(_.isDirectory).toList
//    for(dir <- myDirs){
//      val f_name = (dir + "/" + "inbox").toString
      val sample_dir = "/Users/pratikdomadiya/Desktop/inbox".toString
      val emails = new File(sample_dir).listFiles.filter(_.isFile).filter(x=>  x.getPath.split("/.").last != "DS_Store").toList
      for(email <- emails){
        val df = spark.sparkContext.textFile(email.getPath)//.map(_.split("/\n\n"))//.toString()
//        df.collect().foreach(t=>print(t._2))
//        for (word <- df) println(word.deep)

        val df1 = df.map(_.toString)
        df1.collect().foreach(print)
        var temp = true
        while (temp){

        }

//        df.collect().foreach(f => {
//          if (f.isEmpty)
//            println("8888*****************************")
//          else
//            println(f)
//
//        })
      }
//
//    }
//  }
  spark.stop()
}
