package SparkPractise.myProgram


import org.apache.spark.{SparkContext, SparkConf}


object firstJob extends App{
  
  val conf=new SparkConf().setAppName("firstJob").setMaster("local")
  val sc=new SparkContext(conf)
  
  val rdd=sc.textFile("/Users/AVINASH/Documents/Data/ml-100k/u.data")
  
  val lines=rdd.map(x=>x.toString().split("\t")(2))
  //lines.foreach { println }
  val ratings=lines.countByValue()
  val results=ratings.toSeq.sortBy(_._1)
  
  results.foreach {println}
  
}