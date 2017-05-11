package SparkPractise.myProgram
import org.apache.spark._
object PopularMovie{
  
  def parsedLines(lines:String)={
    val line = lines.split("\t")
    line(1).toInt
  }
  
  def main(args:Array[String])={
    val conf = new SparkConf().setMaster("local").setAppName("PopularMovies")
    val sc = new SparkContext(conf)
    
    val file = sc.textFile("/Users/AVINASH/Documents/Software/Data/ml-100k/u.data")
    
    val lines = file.map(parsedLines)
    /* val fields = file.map(x=>(x.split("\t")(1).toInt,1)
     * 
     */
    val movieId = lines.map(x=>(x,1))
    val totalMovies = movieId.reduceByKey((x,y)=>x+y)
    totalMovies.foreach(println)
    val moviesorted = totalMovies.map(x=>(x._2,x._1)).sortByKey(false)
    moviesorted.foreach(println)
    println("most popular movie")
    val topMovie = moviesorted.take(1).drop(0).foreach(println)
    
    
    
  }
  
  
}