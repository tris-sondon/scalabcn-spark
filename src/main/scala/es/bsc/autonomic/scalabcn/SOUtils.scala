package es.bsc.autonomic.scalabcn

import java.text.SimpleDateFormat

import scala.xml.Elem

/**
 * Jordi Aranda
 * 18/06/14
 * <jordi.aranda@bsc.es>
 */
object SOUtils {

  //Post case class extractor
  val PostRegex = """^Post\((\d+),(\d),(\d+),(\d+),(\d+),(\d+),(\d+),(\d+),(\d+),List\(([\w+-]*[,\s\w+-]*)\),(\d+),(\d+),(\d+),(\d+)\)$""".r
  //Tags extractor
  val TagsRegex = """<[\w-]+>""".r

  object asInt {
    def unapply(s: String) : Option[Int] = Some(s.toInt)
  }

  object asLong {
    def unapply(s: String) : Option[Long] = Some(s.toLong)
  }

  object asList {
    def unapply(s: String) : Option[List[String]] = Some(s.split(",").toList)
  }

  object PostString {
    def unapply(s: String) : Option[Post] = s match {
      case PostRegex(asInt(a), asInt(b), asInt(c), asInt(d), asLong(e), asInt(f), asInt(g), asLong(h), asLong(i), asList(j), asInt(k), asInt(l), asInt(m), asInt(n)) => Some(Post(a, b, c, d, e, f, g, h, i, j, k, l, m, n))
      case _ => None
    }
  }

  object POST_TYPE {
    val QUESTION = 1
    val ANSWER = 2
  }

  val NO_VALUE = 0

  case class Post(id: Int, postTypeId: Int, parentId: Int, acceptedAnswerId: Int, creationDate: Long, score: Int, viewCount: Int, lastEditDate: Long, lastActivityDate: Long, tags: List[String], answerCount: Int, commentCount: Int, favouriteCount: Int, ownerUserId: Int)
  val DUMMY_POST = Post(NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE, List(), NO_VALUE, NO_VALUE, NO_VALUE, NO_VALUE)

  /**
   * Helper function to parse SO dates.
   * @param date A string representing a certain date.
   * @return The number of milliseconds from 1st
   *         January 1970 to the date parsed.
   */
  def parseDate(date: String): Long = {
    val soDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    if(date.equals("")) NO_VALUE else soDateFormat.parse(date).getTime
  }

  /**
   * Helper function to parse tags from a given string.
   * @param tags A string containing different tags.
   * @return A list of strings, one per tag extracted.
   */
  def parseTags(tags: String): List[String] = {
    (for(tag <- TagsRegex findAllIn tags) yield tag.replace("<", "").replace(">", "")).toList
  }

  /**
   * Helper function to create a new user
   * instance from an xml element.
   * @param e The xml post node to parse.
   * @return A new post instance.
   */
  def parseXmlPostElement(e: Elem) : Post = {
    val id = (e \ "@Id").text.toInt
    val postTypeId = (e \ "@PostTypeId").text.toInt
    //Only present if postTypeId == 2
    val parentId = if(postTypeId == POST_TYPE.ANSWER && !(e \ "@ParentId").isEmpty) (e \ "@ParentId").text.toInt else NO_VALUE
    //Only present if postTypeId == 1
    val acceptedAnswerId = if(postTypeId == POST_TYPE.QUESTION && !(e \ "@AcceptedAnswerId").isEmpty) (e \ "@AcceptedAnswerId").text.toInt else NO_VALUE
    val creationDate = parseDate ((e \ "@CreationDate").text)
    val score = if(!(e \ "@Score").isEmpty) (e \ "@Score").text.toInt else NO_VALUE
    //It seems not all posts include the view count field
    val viewCount = if(!(e \ "@ViewCount").isEmpty) (e \ "@ViewCount").text.toInt else NO_VALUE
    val lastEditDate = parseDate ((e \ "@LastEditDate").text)
    val lastActivityDate = parseDate ((e \ "@LastActivityDate").text)
    val tags = parseTags ((e \ "@Tags").text)
    //It seems not all posts include the answer count field
    val answerCount = if (!(e \ "@AnswerCount").isEmpty) (e \ "@AnswerCount").text.toInt else NO_VALUE
    val commentCount = (e \ "@CommentCount").text.toInt
    //It seems not all posts include the favourite count field
    val favouriteCount = if(!(e \ "@FavoriteCount").isEmpty) (e \ "@FavoriteCount").text.toInt else NO_VALUE
    val ownerUserId = if (!(e \ "@OwnerUserId").isEmpty) (e \ "@OwnerUserId").text.toInt else NO_VALUE
    Post(id, postTypeId, parentId, acceptedAnswerId, creationDate, score, viewCount, lastEditDate, lastActivityDate, tags, answerCount, commentCount, favouriteCount, ownerUserId)
  }

}
