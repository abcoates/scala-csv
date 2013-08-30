package org.contakt.csv

import scala.util.parsing.combinator._
import scala.io.BufferedSource
import java.io.{FileInputStream, File}

/**
 * Scala CSV parser, based on code posted by Paul Phillips on Stack Overflow.  Had to modify 'CRLF'.  See http://stackoverflow.com/questions/5063022/use-scala-parser-combinator-to-parse-csv-files
 * Entry points are the 'parse' methods.
 * GitHub: https://github.com/abcoates/scala-csv
 */
object CSV extends RegexParsers {
  override protected val whiteSpace = """[ \t]""".r

  def COMMA   = ","
  def DQUOTE  = "\""
  def DQUOTE2 = "\"\"" ^^ { case _ => "\"" }
  def CR      = "\r"
  def LF      = "\n"
  def CRLF    = "[\r\n]+".r
  def TXT     = "[^\",\r\n]".r

  def file: Parser[List[List[String]]] = repsep(record, CRLF) <~ opt(CRLF)
  def record: Parser[List[String]] = rep1sep(field, COMMA)
  def field: Parser[String] = (escaped|nonescaped)
  def escaped: Parser[String] = (DQUOTE~>((TXT|COMMA|CR|LF|DQUOTE2)*)<~DQUOTE) ^^ { case ls => ls.mkString("")}
  def nonescaped: Parser[String] = (TXT*) ^^ { case ls => ls.mkString("") }

  def parse(s: String): List[List[String]] = parseAll(file, s) match {
    case Success(res, _) => res
    case _ => List[List[String]]()
  }

  def parse(f: File): List[List[String]] = {
    val fsource = new BufferedSource(new FileInputStream(f))
    parse(fsource.getLines.toList.mkString("\n"))
  }

}