package sample

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.sparkproject.dmg.pmml.True

import scala.collection.mutable
import scala.io.Source.fromFile

object Part2_1 {
  def main(args : Array[String]): Unit ={
    val spark = SparkSession.builder()
      .master("spark://192.168.2.23:7077")
      .appName("SparkCreateTableExample")
      .config("spark.sql.warehouse.dir", "hdfs://localhost:9000/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://localhost:9083")
      .config("spark.shuffle.service.enabled", "false")
      .config("spark.dynamicAllocation.enabled", "false")
      .config("spark.cores.max", 3)
      .config("spark.executor.memory", "1g")
      .enableHiveSupport()
      .getOrCreate()
    import spark.sqlContext.implicits._
    val schema = StructType(
      StructField("ID", IntegerType, true) ::
        StructField("To_", StringType, true) ::
        StructField("Message", StringType, true) :: Nil)
    var emptyDF = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], schema)
    println("empty df : ", emptyDF.schema) // empty dataframe

    val source = "/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/merged.txt"//merged_copy.txt"
    val lines = fromFile(source).getLines.map(_.trim)
    var count = 0
    var temp=true
    var msg_pointer = false
    var flag = true
    var builder = new StringBuilder()
    while (lines.hasNext && temp) {
      var line = lines.next().trim()
      if (line.length==0 && flag==true){
        msg_pointer = true
        builder ++= "***$$$$$$$$$START"
        line = lines.next().trim()
        flag=false
      }
      if(line.startsWith("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")){
        builder ++= "***$$$$$$$$$END"
        count = count + 1
        println("record no:", count, "record : ", builder)
        var list_email = process_email(builder, spark, count)
        //        println(list_email.getClass," : ",("asdasd","asdasd").getClass)
        var df1 = List(list_email).toDF("ID", "To_","Message")
        emptyDF = emptyDF.union(df1)
        //        println("list email : ",list_email)
        builder.setLength(0)
        // reinitiate parameters for next message
        msg_pointer=false
        if (lines.hasNext){
          line = lines.next().trim()
        }
        flag=true
      }
      if (msg_pointer == true) {
        builder ++= line + "\n"

      }
      else {
        builder ++= line
      }

    }//while

    def process_email(builder: StringBuilder, spark: SparkSession, count: Int): Tuple3[Int, String, String] = {
//      println("" : builder)
      var From = ""
      var To = ""
      val index_to = builder.indexOf("To:")
      val index_subject = builder.indexOf("Subject:")
      println("index_to :", index_to, "index_sub", index_subject)
      if (index_to < index_subject) {
        From = builder.substring(start = builder.indexOf("From:") + "From:".length, end = builder.indexOf("To:")).trim()
        To = builder.substring(start = builder.indexOf("To:") + "To:".length, end = builder.indexOf("Subject:")).trim()
      } else {
        From = builder.substring(start = builder.indexOf("From:") + "From:".length, end = builder.indexOf("Subject:")).trim()
      }
      val msg  = builder.substring(start = builder.indexOf("***$$$$$$$$$START") + "***$$$$$$$$$START".length, end = builder.indexOf("***$$$$$$$$$END")).trim()
      //create a tuple of the data variables
      var seq = (count, To, msg)
      return seq
    }//process_email

    emptyDF.show()
  }//main

}//part2_1
