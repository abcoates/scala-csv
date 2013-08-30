package org.contakt.csv.test

import org.scalatest.{BeforeAndAfter, FlatSpec}
import org.contakt.csv.CSV
import java.io.{FileInputStream, File}
import scala.io.BufferedSource

/**
 * Tests for CSV object.
 */
class CSVSuite extends FlatSpec with BeforeAndAfter {

  val csvFile = new File("res/csv/csv-test-file.csv")
  var csvString:Option[String] = None

  before {
    assert((csvFile exists) && (csvFile canRead), "CSV file does not exist or cannot be read: " + csvFile.getAbsolutePath)
    val csvIn = new BufferedSource(new FileInputStream(csvFile))
    val csvLines = csvIn.getLines.toList
    // println("CSV line count = " + csvLines.size)
    csvString = Some(csvLines.mkString("\n"))
    assert(csvString.getOrElse("").length >= 1, "failed to read CSV data from CSV file: " + csvFile.getAbsolutePath)
    // println("CSV data:\n" + csvString.get)
  }

  "The CSV object" should "be able to parse a 1-cell CSV string" in {
    val csv = "a"
    assert(CSV.parse(csv) === List(List("a")), "1-cell CSV data did not match")
  }

  it should "be able to parse a 1-line CSV string" in {
    val csv = "a,b, c , d"
    assert(CSV.parse(csv) === List(List("a", "b", "c", "d")), "1-line CSV data did not match")
  }

  it should "be able to parse a 1-cell CSV string with newlines" in {
    val csv = "\"b\nb\""
    assert(CSV.parse(csv) === List(List("b\nb")), "1-cell CSV data with newlines did not match")
  }

  it should "be able to parse a 1-cell CSV string with double quotes" in {
    val csv = "\"b\"\"b\""
    assert(CSV.parse(csv) === List(List("b\"\b")), "1-cell CSV data with double quotes did not match")
  }

  it should "be able to parse a CSV file with newlines and double quotes in cells" in {
    val expected = List(
      List("A","B","C","D"),
      List("a","b","c","d"),
      List("aa","b\nb","c\nc","dd"),
      List("aaa","b\n\"\n\"\"\n\"\"\"\nb","c\n\"\n\"\"\n\"\"\"\nc","ddd"),
      List("aaaa","bbbb","cccc","dddd")
    )
    assert(CSV.parse(csvString.get) === expected, "CSV data with newlines and double quotes did not match")
  }

}
