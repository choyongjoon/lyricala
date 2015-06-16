import com.twitter.penguin.korean.TwitterKoreanProcessor
import com.twitter.penguin.korean.phrase_extractor.KoreanPhraseExtractor.KoreanPhrase
import com.twitter.penguin.korean.tokenizer.KoreanTokenizer.KoreanToken
import scala.collection.immutable.ListMap
import java.io._

object FromTxt {

  def main(args: Array[String]) {
    if (args.length < 1) {
      println("No file name. Exit.")
      return
    }
    val file_name = args(0)
    val source = scala.io.Source.fromFile(file_name) // 줄리아 하트
    val lines = try source.mkString finally source.close()

    val normalized: CharSequence = TwitterKoreanProcessor.normalize(lines)
    val tokens: Seq[KoreanToken] = TwitterKoreanProcessor.tokenize(normalized)
    // KoreanToken
    // text: String, pos: KoreanPos, offset: Int, length: Int, unkown: Boolean

    // 1. stem
    val stemmed: Seq[KoreanToken] = TwitterKoreanProcessor.stem(tokens)
    val grouped = stemmed.groupBy(x => "(" + x.pos.toString + ") " + x.text)

    // 2. phrase
    // val phrases: Seq[KoreanPhrase] = TwitterKoreanProcessor.extractPhrases(tokens)
    // val grouped = phrases.groupBy(x => x.text)

    val counts = grouped.mapValues(x => x.length)
    val sorted_counts = ListMap(counts.toSeq.sortWith(_._1 < _._1):_*)
    val print_format = sorted_counts mkString "\n"
    printToFile(new File("result_" + file_name)) { p =>
        p.println(print_format)
    }
  }

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(f)
        try { op(p) } finally { p.close() }
  }
}
