package SparkPractise.myProgram

import org.apache.spark._
import org.apache.log4j._

object FavHero {
  
   def main(args:Array[String]){
    
     Logger.getLogger("org").setLevel(Level.ERROR)
    
   val conf = new SparkConf().setMaster("local[1]").setAppName("MostPopularMovie")
   val sc = new SparkContext(conf)
   
   //val file = sc.textFile("/Users/AVINASH/Desktop/data.rtf")
   val file = """Josh,San Jose,Captain America,Superman,Hulk
Ashley, Fremont, Batman,Spiderman,Ironman
Josh , Los Angeles,Hulk,Thor,Wolverine
Josh, Fremont, Hercules, Hulk, Spiderman
""" 
   
   val re = sc.parallelize(file.split("\n"))
 
   
   //val lines = re.map(_.split(",")).map(
   /*val lines = re.flatMap(_.split(","))
   val line = lines.map(x=>(x,1)).reduceByKey(_+_)
   line.foreach(println)*/
   
   
   val lines = re.flatMap { r=>
     val Array(name, city, choice1, choice2, choice3) = r.split(",");
     List(((name, choice1),1), ((name, choice2),1), ((name, choice3),1))
   }.reduceByKey(_+_)
     
     lines.foreach(println)
     
     //val r3 = lines.reduceByKey((x,y)=>x+y)
     
   //val l = r3.filter{ case((x,_),_) => x == "Josh"}
   //r3.foreach(println)
   
   //val r = lines.groupByKey()
   //r.foreach(println)
  
    // val r4 = r3.map{ case((name, choice), total) => (name, (choice, total))}
     //r4.foreach(println)
     
     //val res = r4.reduceByKey((x,y)=> if(y._2>x._2) y else x)
   
  }
}