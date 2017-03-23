package SparkPractise.myProgram

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext


object avgNumberofFrnds {
 def main(args: Array[String])={
  val conf=new SparkConf().setMaster("local").setAppName("Avg number of friends for an age")
  val sc=new SparkContext(conf)
  
  val file=sc.textFile("./fakefriends.csv")
  
  val rdd=file.map(parseLines)
  
  val rd=rdd.mapValues( x=>(x,1)).reduceByKey((x,y)=>(x._1+y._1,x._2+y._2))
  
  val avgrdd=rd.mapValues(x=>(x._1/x._2))
  
  val sortedrdd=avgrdd.sortByKey() 
  
  sortedrdd.foreach(println)

 }
 
 def parseLines(line: String)={
   val lines=line.split(",")
   
   val age=lines(2).toInt
   val frnds=lines(3).toInt
   
   (age,frnds)
   
 }
}