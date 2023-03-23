package sample

import org.apache.spark.sql.SparkSession

object Part1_3_hive_queries {
  def main(args: Array[String]): Unit = {
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

    val df=spark.sql(
      """
        select concat_ws(',',collect_set(trim(item_id))) as email_ids
        from sample.email lateral view explode(split(from_,',')) a as item_id
        union
        select concat_ws(',',collect_set(trim(item_id_to))) as email_ids
              from sample.email lateral view explode(split(to_,',')) a as item_id_to
        """)
    df.show()
    var total_distinct_email=0
    for(line <- df.collect()){
      total_distinct_email = total_distinct_email + line.toString().split(",").map(_.trim).length
    }
    println("*****"*10)
    println("Total distinct email from FROM and TO",total_distinct_email)
    println("*****"*10)

    val total_email = spark.sql(
      """select from_,count(*) as Total_emails_sent
        from sample.email e group by e.from_""")
    println("*****" * 10)
    println("Total email sent by persons who are listed under 'From' ")
    println("*****" * 10)
    total_email.show()

    val df_subject = spark.sql("select from_ from sample.email where Subject LIKE '%request%'")
    df_subject.show()
    spark.stop()

  }



}
