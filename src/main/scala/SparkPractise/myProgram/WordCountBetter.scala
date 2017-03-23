package SparkPractise.myProgram

import org.apache.spark.{SparkContext,SparkConf}

object WordCountBetter {
  
  def main(args:Array[String])={
    val conf=new SparkConf().setMaster("local").setAppName("word count modified")
    val sc=new SparkContext(conf)
    
    val file=sc.textFile("/Users/AVINASH/Documents/Notes PDF/Spark by Udemy/SparkScala/book.txt")
    
    val lines=file.flatMap(x=>x.split("\\W+"))
    
    val maplines=lines.map(x=>x.toLowerCase())
    
    val wordCounts=maplines.map(x=>(x,1)).reduceByKey((x,y)=>x+y)
    
    //val count=maplines.countByValue()
    
    //count.toSeq.sortBy(_._2).foreach(println)
    //wordCounts.sortBy(_._2).foreach(println)
    
    wordCounts.map(x=>(x._2,x._1)).sortByKey().foreach(println)
    
  }
  
}