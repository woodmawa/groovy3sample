package com.softwood.worksheet.io

import com.softwood.worksheet.Table

interface DataReader<O extends ReadOptions> {
    Table read (Source source)
    Table read (O options)
}