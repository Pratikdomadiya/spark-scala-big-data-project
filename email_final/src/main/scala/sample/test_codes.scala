package sample

import org.apache.spark.sql.SparkSession

import scala.collection.mutable

object test_codes {
  def main(args:Array[String]): Unit ={


    val line = new StringBuilder()
     line ++=  """start: asadsd
         second line

         thirdline
         end:
        """//.trim

    val From = line.substring(start = line.indexOf("start:") + "start:".length, end = line.indexOf("end:")).trim()
    print(From)
//    println(line.count("//n"))
  val spark = SparkSession.builder()
    .master("spark://192.168.2.23:7077")
    .appName("SparkCreateTableExample")
    .config("spark.sql.warehouse.dir", "hdfs://localhost:9000/user/hive/warehouse")
    .config("hive.metastore.uris", "thrift://localhost:9083")
    .config("spark.shuffle.service.enabled", "false")
    .config("spark.dynamicAllocation.enabled", "false")
    .config("spark.cores.max", 3)
    .config("spark.executor.memory", "3g")
    .enableHiveSupport()
    .getOrCreate()
    import spark.sqlContext.implicits._
    var email = "sddsdsdsd"
    var a : List[(Int,String)] = List()
    a = a:+((1,"pratik"))
    a = a:+((1,"pratik1"))

    println("$$$$$$$$$$$$$$$$$$$$$$$$,",a,(1,email).getClass)
    a.foreach(println)

    var df_new = a.toDF("Id","emailIds")
    df_new.show()

  }

}
