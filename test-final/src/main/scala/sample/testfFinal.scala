package sample
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
object testFinal {
  def main(args: Array[String]) {

    val spark = SparkSession.builder()
      .master("spark://192.168.2.23:7077")
      .appName("SparkCreateTableExample")
      .config("spark.sql.warehouse.dir", "hdfs://localhost:9000/user/hive/warehouse")
      .config("hive.metastore.uris", "thrift://localhost:9083")
      .config("spark.shuffle.service.enabled", "false")
      .config("spark.dynamicAllocation.enabled", "false")
      .config("spark.cores.max",3)
      .config("spark.executor.memory","1g")
      .enableHiveSupport()
      .getOrCreate()
//    import spark.sqlContext.implicits._


    //    spark.sql("create database pra_tik_1234;")
    val df = spark.sql("show databases;")
//    spark.sql("CREATE TABLE IF NOT EXISTS sample.email")

    print(df.show())
//    var df1 = List(("2018-01-11", "123")).toDF("timestamp", "col2")
//    df1.show()
////    spark.sql(
//      "CREATE TABLE sample.department( department_id int , department_name string)")
    spark.sql("INSERT INTO sample.department values (101,'Oncology')")

    val f = spark.sql("SELECT * FROM sample.department")
    print(f.show())

    spark.stop()
  }
}

