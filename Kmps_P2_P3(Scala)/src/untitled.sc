

/*def append1 (xs:List[Any],ys:List[Any]) : List[Any] = xs match {
  case Nil => ys
  case y::zs => y::append1(zs,ys)
}
val a =append1(4::Nil,1::3::5::6::Nil)
*/








import com.sun.tools

import java.nio.file.{Files, Paths}
import scala.List
//import scala.collection.immutable.IntMap.Nil.mkString
import scala.io.Source

case class Track(title: String, length: String, rating: Int, features: List[String], writers: List[String])
case class Album(title: String, date: String, artist: String, tracks: List[Track])

val albums =  Source.fromFile("C:\\Users\\Hassen\\IdeaProjects\\p2\\src\\alben.xml").mkString.toCharArray.toList

def createTokenListAuserhalbKlammer(source: List[Char], meinString: String):String = source match {
  case '\n'::rest =>  createTokenListAuserhalbKlammer(rest, meinString)
  case '\t'::rest =>  createTokenListAuserhalbKlammer(rest, meinString)
  case '\r'::rest =>  createTokenListAuserhalbKlammer(rest, meinString)
  case '<'::rest => meinString
  case x::rest => createTokenListAuserhalbKlammer(rest, meinString+x)
  case Nil => ""
}

def createTokenListInKlammer(source: List[Char], meinString: String): String = source match {
  case '\n'::rest =>  createTokenListInKlammer(rest, meinString)
  case '\t'::rest =>  createTokenListInKlammer(rest, meinString)
  case '\r'::rest =>  createTokenListInKlammer(rest, meinString)
  case '>' ::  rest => meinString
  case x::rest => createTokenListInKlammer(rest, meinString+x)
  case Nil => ""
}
def createTokenList(source: List[Char]): List[String] = source match {
  case Nil => Nil

 case '\n'::rest =>  createTokenList(rest)
  case '\t'::rest =>  createTokenList(rest)
  case '\r'::rest =>  createTokenList(rest)
  //case '\u2424'::rest =>  createTokenList(rest)


  case '<'::rest => createTokenListInKlammer(rest,"") ::  createTokenList((rest))
  case '>'::rest => createTokenListAuserhalbKlammer(rest,"") ::  createTokenList((rest))
  case _::rest =>  createTokenList(rest)


}

createTokenList(albums)
/*def findEndofAlbum(source: List[String]): List[String] = source match{
  case "/album"::rest => rest
  case _::rest => findEndofAlbum(rest)
  case Nil => Nil
}
def findEndofTrack(source: List[String]): List[String] = source match{
  case "/track"::rest => rest
  case _::rest => findEndofTrack(rest)
  case Nil => Nil
}


def putinlist(ll:List[String],S:String): List[String] ={
  ll:+S
  return ll
}*/
def parseFeatureList(source: List[String], myFeatureList: List[String]):List[String] = source match {
  case "/feature"::rest => myFeatureList
  case x::rest => myFeatureList :+ x
  case Nil => myFeatureList
}

def parseWritingList(source: List[String], myFeatureList: List[String]):List[String] = source match {
  case "/writing"::rest => myFeatureList
  case x::rest => myFeatureList :+ x
  case Nil => myFeatureList
}


/*Track List*/

def parseTrack(source: List[String],  meineTrackList: List[Track], meinTrack: Track):List[Track]  = source match {
  case "feature"::rest => parseTrack(rest,meineTrackList,  meinTrack.copy(features=parseFeatureList(rest,meinTrack.features)))
  case "writing"::rest => parseTrack(rest,  meineTrackList,meinTrack.copy(writers=parseWritingList(rest,meinTrack.writers)))
  case "title"::element::rest => parseTrack(rest,  meineTrackList,meinTrack.copy(title = element))
  case "length"::element::rest => parseTrack(rest,  meineTrackList,meinTrack.copy(length = element))
  case "rating"::element::rest => parseTrack(rest, meineTrackList, meinTrack.copy(rating = element.toInt))
  case "/track"::rest => meineTrackList :+ meinTrack
  case _::rest => parseTrack(rest,meineTrackList,meinTrack)
  case Nil => Nil
}


def parseAlbum(source: List[String], meinAlbum: Album):Album = source match {
  case "track"::rest => parseAlbum((rest),meinAlbum.copy(tracks=parseTrack(rest, meinAlbum.tracks,Track("","",0,Nil,Nil))))
  case "title"::element::rest => parseAlbum(rest, meinAlbum.copy(title=element))
  case "artist"::element::rest => parseAlbum(rest, meinAlbum.copy(artist=element))
  case "date"::element::rest => parseAlbum(rest, meinAlbum.copy(date=element))
  case "/album"::rest => meinAlbum
  case _::rest => parseAlbum(rest, meinAlbum)
  case Nil => meinAlbum
}

def parseFile(source:List[String]): List[Album] = source match {
      case "album"::rest => parseAlbum(rest, Album("","","",Nil))::parseFile((rest))
      case _::rest => parseFile(rest)
      case Nil => Nil
}



// Aufgabe 1
val result= createTokenList(albums)
val result1=parseFile(result)

def map[A](input_List: List[A],func :A =>A) : List[A]=input_List match {
  case Nil =>Nil
  case y::ys => func(y)::map(ys,func)
}
map[String]( "a"::"b"::"c"::"d"::Nil,x => x.toUpperCase())


val Result1 = map[Album](result1,x => x.copy(title = x.title.toUpperCase))

val Resultat1c = map[Album](result1,x => {val aktuellesAlbum = x.copy(title = x.title.toUpperCase); aktuellesAlbum.copy(tracks = map[Track](x.tracks,x => x.copy(title = x.title.toUpperCase)))})



def poly_map[A,B](input_List: List[A],func :A =>B) : List[B]=input_List match {
  case Nil =>Nil
  case y::ys => func(y)::poly_map(ys,func)
}

val Laenge_der_Tracks = poly_map[Album, List[String]](result1,x => poly_map[Track, String]( x.tracks,x => x.length))

//erster Album
val album2 = result1(1)

// Aufgabe 2

def filter[A](input_list:List[A],condition: A=>Boolean):List[A]=
  input_list match {
    case Nil =>Nil
    case y::ys => if(condition(y))  y::filter(ys,condition )  else filter(ys , condition)
  }
val mytracks=album2.tracks
val aufgabe2_2= filter[Track](mytracks,mytrack=> mytrack.rating>=4)


val test = poly_map[Track, String]( filter[Track](album2.tracks,x => x.writers.contains("Rod Temperton") ), x => x.title)

// Aufgabe 3
def partition[A](input_list:List[A],condition:A=>Boolean):List[List[A]]=

  input_list match{

    case Nil=>List(List())
    case x::xs =>   condition(x) match {
      case true => List()::partition(xs,condition )
      case false =>  val y = partition( xs,condition); (x::y.head)::y.tail
    }
  }
def isBlank(s: String): Boolean = s.trim.isEmpty
partition [Char](List('A','b','c','K','e','E'), x=>if (x.isUpper) true else false )
partition[Track](album2.tracks,x=> if (x.title=="Thriller") true else false)
val zw = partition[Char](albums,x=> if (x=='<' || x=='>')true else false )
val poly=poly_map[List[Char],String](zw,Y=>Y.mkString)

filter[String](poly_map[List[Char],String](partition[Char](albums,x=> if (x=='<' || x=='>')true else false ),Y=>Y.mkString),x=> !isBlank(x))


//Aufgabe 4

def sum(f1:Int=>Int,f2:(Int,Int)=>Int,a:Int,b:Int,default:Int):Int= {
  if (a > b) default
  else if (a==b) a
  else f2( f1(a),sum(f1,f2,a+1,b, default) )

}
sum(x=>x,(x,y)=>x*y,5,5,0)


//d
def fold(operation: (Int,Int)=>Int, default: Int, source: List[Int]): Int = source match {
  case Nil => default
  case x::xs => operation(x, fold(operation, default, xs))
}

def range(a:Int, b:Int): List[Int] = {
  if (a == b) List(b) else a::range(a+1,b)}


val result_d = fold((x,y)=>x*y,1,map[Int](range(1,5),x=>x ))

//b- right-folging weil was zuerst ausgeführt wird, ist unsere sum Funktion also was drinnen ist
//c- wenn a=b wird a ausgegeben . Sinnvoller wäre eine textuelle Ausgabe wie "Wertebereich ist leer versuchen Sie es nochmal"