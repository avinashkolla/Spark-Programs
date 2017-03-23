package SparkPractise.myProgram

import org.apache.spark._
import org.apache.log4j._
import scala.io.Codec
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.math.sqrt
import org.apache.spark.rdd._

object MovieSimilaritiesCluster {
  type movieRating=(Int,Double)
  type userRating=(Int,(movieRating,movieRating))
  
   type RatingPair = (Double, Double)
  type RatingPairs = Iterable[RatingPair]
  
  def loadMovieNames():Map[Int,String]={
    implicit val codec=Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
   codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    val lines=Source.fromFile("us3n://sparkpractise/data/movies.dat").getLines()
    var movieNames:Map[Int,String]=Map()
    for(line<-lines){
      var fields=line.split("::")
      if(fields.length>1){
      movieNames +=(fields(0).toInt->fields(1))
      }
    }
    return movieNames
  }
  
  def filterDuplicates(userrating:userRating):Boolean={
    val movierating1=userrating._2._1
    val movierating2=userrating._2._2
    
    val movie1=movierating1._1
    val movie2=movierating2._2
    
    return movie1<movie2
    
  }
  
  def mapped(userrating:userRating)={
    val movieRatings1=userrating._2._1
    val movieRatings2=userrating._2._2
    
    val movie1=movieRatings1._1
    val movie2=movieRatings2._1
    val rating1=movieRatings1._2
    val rating2=movieRatings2._2
    
    ((movie1,movie2),(rating1,rating2))
  }
  
  def computeSimilarites(ratings:RatingPairs):(Double,Int)={
    var sumPoints=0
    var sumxx:Double=0.0
    var sumyy:Double=0.0
    var sumxy:Double=0.0
    
    for(pair<-ratings){
      val ratingsX=pair._1
      val ratingsY=pair._2
      
      sumxx+=ratingsX*ratingsY
      sumxy+=ratingsX*ratingsY
      sumyy+=ratingsX*ratingsY
    }
    
    val numerator:Double=sumxy
    val denominator:Double=sqrt(sumxx)+sqrt(sumxy)
    var score:Double = 0.0
    if (denominator != 0) {
      score = numerator / denominator
    }
    
    return (score, sumPoints)
  }
  
  def main(args:Array[String])={
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    val conf=new SparkConf().setAppName("MovieSimilaritiesCluster")
    val sc = new SparkContext(conf)
    
    val nameDict = loadMovieNames()
    val file=sc.textFile("s3n://sparkpractise/data/ratings.dat")
    val lines=file.map(x=>x.split("\t")).map(x=>(x(0).toInt,(x(1).toInt,x(2).toDouble)))
   
    val joined=lines.join(lines)
    //joined.foreach(println)
    val uniques=joined.filter(filterDuplicates)
    val pairedUniques=uniques.map(mapped)
    val grouped=pairedUniques.groupByKey()
    val movieSimilarities=grouped.mapValues(computeSimilarites).cache()
    //Save the results if desired
    //val sorted = moviePairSimilarities.sortByKey()
    //sorted.saveAsTextFile("movie-sims")
    
    // Extract similarities for the movie we care about that are "good".
    
    if (args.length > 0) {
      val scoreThreshold = 0.97
      val coOccurenceThreshold = 50.0
      
      val movieID:Int = args(0).toInt
      
      // Filter for movies with this sim that are "good" as defined by
      // our quality thresholds above     
      
      val filteredResults = movieSimilarities.filter( x =>
        {
          val pair = x._1
          val sim = x._2
          (pair._1 == movieID || pair._2 == movieID) && sim._1 > scoreThreshold && sim._2 > coOccurenceThreshold
        }
      )
        
      // Sort by quality score.
      val results = filteredResults.map( x => (x._2, x._1)).sortByKey(false).take(10)
      
      println("\nTop 10 similar movies for " + nameDict(movieID))
      for (result <- results) {
        val sim = result._1
        val pair = result._2
        // Display the similarity result that isn't the movie we're looking at
        var similarMovieID = pair._1
        if (similarMovieID == movieID) {
          similarMovieID = pair._2
        }
        println(nameDict(similarMovieID) + "\tscore: " + sim._1 + "\tstrength: " + sim._2)
      }
    
    }
  }
  
}