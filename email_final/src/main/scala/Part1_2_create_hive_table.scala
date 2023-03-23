import org.apache.spark.sql.catalyst.expressions.aggregate.Count
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import scala.collection.mutable
import scala.io.Source.fromFile
object Part1_2_create_hive_table  {
  def main(args: Array[String]) {
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
    val source = "/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/merged_head.txt"
//    var df = spark.read.textFile(source)
    //Define your schema
    val schema = StructType(
        StructField("ID", IntegerType, true)::
        StructField("Message_ID", StringType, true) ::
        StructField("Date", StringType, true) ::
        StructField("From", StringType, true) ::
        StructField("To", StringType, true) ::
        StructField("Subject", StringType, true) ::
        StructField("Mime_Version", StringType, true) ::
        StructField("Content_Type", StringType, true) ::
        StructField("Content_Transfer_Encoding", StringType, true) ::
        StructField("X_From", StringType, true) ::
        StructField("X_To", StringType, true) ::
        StructField("X_cc", StringType, true) ::
        StructField("X_bcc", StringType, true) :: Nil)

    var emptyDF= spark.createDataFrame(spark.sparkContext.emptyRDD[Row],schema)
    println("empty df : ",emptyDF.schema) // empty dataframe




    val lines = fromFile(source).getLines.map(_.trim)
    var temp=true
    var builder = new StringBuilder()
    var Message_ID = 0:Int
    var Date = 0
    var From = 0
    var To = 5290
    var Subject = 0
    var Mime_Version = 0
    var Content_Type = 0
    var Content_Transfer_Encoding = 0
    var X_From = 0
    var X_To = 0
    var X_cc = 0
    var X_bcc = 0
    var count = 0
    while (lines.hasNext && temp) {
      var line = lines.next().trim()
      if (line.startsWith("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")){
        builder++="***$$$$$$$$$"
        count=count+1
        println("record no:", count,"record : ",builder)
        var list_email = process_email(builder,spark,count)
//        println(list_email.getClass," : ",("asdasd","asdasd").getClass)
        var df1 = List(list_email).toDF("ID","Message_ID", "Date", "From", "To", "Subject", "Mime_Version", "Content_Type", "Content_Transfer_Encoding", "X_From", "X_To", "X_cc", "X_bcc")
        emptyDF=emptyDF.union(df1)
//        println("list email : ",list_email)
        builder.setLength(0)
      }else{
        builder++=line
      }

    }


    def process_email(builder:StringBuilder, spark:SparkSession, count: Int):Tuple13[Int,String,String,String,String,String,String,String,String,String,String,String,String]  ={
      val msg_id = builder.substring(start = builder.indexOf("Message-ID:")+"Message-ID:".length, end = builder.indexOf("Date:")).trim()
      val date = builder.substring(start = builder.indexOf("Date:")+"Date:".length, end = builder.indexOf("From:")).trim()
      var From = ""
      var To = ""
      val index_to = builder.indexOf("To:")
      val index_subject = builder.indexOf("Subject:")
      println("index_to :",index_to,"index_sub", index_subject)
      if (index_to < index_subject){
        From = builder.substring(start = builder.indexOf("From:")+"From:".length,end = builder.indexOf("To:")).trim()
        To = builder.substring(start = builder.indexOf("To:")+"To:".length,end = builder.indexOf("Subject:")).trim()
      }else{
        From = builder.substring(start = builder.indexOf("From:")+"From:".length,end = builder.indexOf("Subject:")).trim()
      }

      val Subject = builder.substring(start = builder.indexOf("Subject:")+"Subject:".length, end = builder.indexOf("Mime-Version:")).trim()
      val Mime_Version = builder.substring(start = builder.indexOf("Mime-Version:")+"Mime-Version:".length, end = builder.indexOf("Content-Type:")).trim()
      val Content_Type = builder.substring(start = builder.indexOf("Content-Type:")+"Content-Type:".length, end = builder.indexOf("Content-Transfer-Encoding:")).trim()
      val Content_Transfer_Encoding = builder.substring(start = builder.indexOf("Content-Transfer-Encoding:")+"Content-Transfer-Encoding:".length, end = builder.indexOf("X-From:")).trim()
      val X_From = builder.substring(start = builder.indexOf("X-From:")+"X-From:".length, end = builder.indexOf("X-To:")).trim()
      val X_To = builder.substring(start = builder.indexOf("X-To:")+"X-To:".length, end = builder.indexOf("X-cc:")).trim()
      val X_cc = builder.substring(start = builder.indexOf("X-cc:")+"X-cc:".length, end = builder.indexOf("X-bcc:")).trim()
      val X_bcc = builder.substring(start = builder.indexOf("X-bcc:")+"X-bcc:".length, end = builder.indexOf("***$$$$$$$$$")).trim()

      //create a tuple of the data variables
      var seq = (count,msg_id, date, From, To, Subject, Mime_Version, Content_Type, Content_Transfer_Encoding, X_From, X_To, X_cc, X_bcc) //,["Message_ID","Date" ,"From","To" ,"Subject" ,"Mime_Version","Content_Type" ,"Content_Transfer_Encoding", "X_From" ,"X_To" ,"X_cc", "X_bcc" ])
      return seq
    }
//    val mss_id = builder.substring(star t = builder.indexOf("To:"),end = builder.indexOf("Subject:"))
    println(Message_ID)
    print(emptyDF.show())

//    val op = emptyDF.write.mode("overwrite").format("csv").save("/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/sample")
//    emptyDF.write.option(key = "header",value = true)
    emptyDF.coalesce(1).write.option("header", true).option("delimiter", "|").csv("/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/sample")
    print("complete code")
    // Read csv file with schema information.
//    val df = spark.read.option("header", true).option("delimiter", "|").option("inferSchema", true).csv("/Volumes/D/F/Loyalist/S--A_ZAA-N/course contents/26-feb-micro-services/Final_email/sample/sample.csv")
//    df.show()
    emptyDF.write.saveAsTable("sample.email")
//
    spark.close()
  }

}