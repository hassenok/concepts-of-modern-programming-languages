case class Track(title: String, length: String, rating: Int, features: List[String], writers: List[String])
case class Album(title: String, date: String, artist: String, tracks: List[Track])
def append1 (xs:List[Char],ys:List[Char]) : List[Char] = xs match {
  case Nil => ys
  case y::zs => y::append1(zs,ys)
}
//append1(hello::Nil,world::Nil)