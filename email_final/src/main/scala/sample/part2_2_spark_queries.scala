package sample

import org.apache.derby.iapi.types.Like
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{array, col, column, desc, expr, lit, max, row_number}
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import java.io._
import java.io.{BufferedWriter, FileWriter}

object part2_2_spark_queries {
  def main(args:Array[String]): Unit ={
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

    //     Question 1 ################################################:

    val df = spark.sql(
      """
        select concat_ws(',',collect_set(trim(item_id_to))) as email_ids
              from sample.email lateral view explode(split(to_,',')) a as item_id_to
        """)
//    println(df.show())
    val total_distinct_email = df.first().apply(0).toString().split(",").map(_.trim())
    println(" Type of total_distinct_email : ",total_distinct_email.getClass(),total_distinct_email.length)
    var count = 0
    var temp_list : List[(Int,String)] = List()
    for (email <- total_distinct_email){
      count=count+1
//      var df_new = List((count,email)).toDF("Id","emailIds") // create a List of Seq and then create a Dataframe
      println(count,email)
      temp_list = temp_list :+ (count,email)
//      emptyDF = emptyDF.union(df_new)
    }
    var df_new1 = temp_list.toDF("Id","emailIds")
    df_new1.show()
    // write dataframe to parquet file on hive warehouse (hdfs location)
    df_new1=df_new1.coalesce(1)// create only one partition while writing to file - so read and write would preserve order of the records
    df_new1.write.mode(SaveMode.Overwrite).parquet("hdfs://localhost:9000/user/hive/warehouse/people.parquet") // overwrite file in hdfs

    //read dataframe to parquet file on hive warehouse (hdfs location)
    val parquetFileDF = spark.read.parquet("hdfs://localhost:9000/user/hive/warehouse/people.parquet")
    parquetFileDF.show()



//     Question 2 ################################################:
    val df_ = spark.sql(
      """
        select concat_ws(',',collect_list(trim(item_id_to))) as email_ids
              from sample.email lateral view explode(split(to_,',')) a as item_id_to
        """)
    val total_emails = df_.first().apply(0).toString().split(",").map(_.trim())
    println(" Type of total_distinct_email : ",total_distinct_email.getClass(),total_distinct_email.length)
//    df.groupBy('value').count().show()
    var count_ = 0
    var temp_list1: List[(Int, String)] = List()
    for (email <- total_emails) {
      count_ = count_ + 1
      //      var df_new = List((count,email)).toDF("Id","emailIds") // create a List of Seq and then create a Dataframe
      println(count_, email)
      temp_list1 = temp_list1 :+ (count_, email)
      //      emptyDF = emptyDF.union(df_new)
    }
    var df_new = temp_list1.toDF("Id", "emailIds")
    df_new.show()
    println(df_new.getClass)
    val df_2 = df_new.groupBy(col("emailIds").alias("email address")).count().select(column("email address"),
      column("count").as("total email received"))
    df_2.show()
//    df.groupBy('emailIds')//.count().select(func.col("emailIds").alias("distinct_country"),func.col("count").alias("country_count")).show()

//     df_2.agg(max(col("total email received")).as("total email received")).select(column("email address").as("max email receiver name"),column("Total email received")).show()
    println(" columns : ",df_2.columns)
    val allColumnNames = df_2.columns
    println(allColumnNames.mkString(",")) // show column names of dataframe

    //     Question 3 ################################################:

    val df_3 = df_new.groupBy(col("emailIds").alias("max email receiver user")).count()
      .orderBy(desc("count")).select(column("max email receiver user"),
      column("count").as("total email received"))
    df_3.show(1)

    //     Question 4 ################################################:

    val df_4 = df_new.filter(col("emailIds").like("%request%"))
    df_4.show()
    spark.close()
  }//main

}//part2_2_spark_queries
