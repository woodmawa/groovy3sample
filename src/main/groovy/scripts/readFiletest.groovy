package scripts

import com.softwood.worksheet.DatasetRow
import com.softwood.worksheet.TableHashMapImpl
import com.softwood.worksheet.io.DataFrameReader
import com.softwood.worksheet.io.ReaderRegistry
import com.softwood.worksheet.io.Source
import com.softwood.worksheet.io.csv.CsvReadOptions
import com.softwood.worksheet.io.csv.CsvReader

TableHashMapImpl table = new TableHashMapImpl ()


//instance of api use - table read - returns dataFrameReader which has
//csv method, invoked with file name here.  This invoked CSVoptions instances which
// then invokes the final csv(option) which invokes the read
//this will pick up default options only
table = TableHashMapImpl.read().csv ("testDataFile.csv")

//alternative use of api is to create a builder and configure it, then use withOptions methid
//def builder = CSVReadOptions.builder()
//builder.configX mthods
//def options = builder.build()
//Table.read().usingOptions (options)

DatasetRow row = table.getRow(0)


println "cells in row : ${row.getCellsAsList()}"

println "--done--"
