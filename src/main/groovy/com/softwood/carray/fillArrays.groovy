package com.softwood.carray

def start = [0,0,0,0]
def end = [1,1,1,1]

def byColumnFirst = [[0,0],[1,0],[0,1][1,1],[0,2],[1,2]]
def byRowFirst = [[0,0],[0,1],[0,2],[1,0][1,1],[1,2]]

def dimensions = end.size()
def entries = byRowFirst.size()

enum ListFill {
    byRowFirst,
    byColumnFirst
}

ListFill approach = ListFill.byRowFirst

//try by row first

def value = start
def numColumns = end[0] - start[0]

def calcArrayIndex (low, hi) {
    assert hi.size() == low.size()

    HashMap arrayIndexLimits =  [:]
    for (i in 0..<hi.size()){
        //start with x, y, z

        def upper = hi[i]
        def lower = low[i]
        def range = [:]  //java.util.LinkedHashMap
        range << [upper: upper]
        range << [lower: lower]
        arrayIndexLimits << [(i): range]
    }

    arrayIndexLimits
}

HashMap arrayIndexLimits =  calcArrayIndex(start, end)


println arrayIndexLimits  //this is map of dimension position and lower and upper bound for each position

def increment = {fillBy, limits, arrayValue, step ->
    ArrayList next = ArrayList.copyOf(arrayValue)
    int startingColumn
    int currentColumn

    switch (fillBy) {
        case ListFill.byColumnFirst:

            startingColumn = 0
            currentColumn = 0

            def highLow = limits[startingColumn]
            int upper = highLow['upper']
            int lower = highLow['lower']

            //start with currentValue as value in the column 0
            def columnValue = arrayValue[currentColumn]

            for (col in 0..<arrayValue.size()) {
                if (columnValue < upper) {
                    next[currentColumn] = columnValue + step
                    break
                }
                else {
                    next[currentColumn++] = lower
                    columnValue =next[currentColumn] //get the start value point for next column
                    highLow = limits[currentColumn]
                    upper = highLow['upper']
                    lower = highLow['lower']
                }
            }
            break

        case ListFill.byRowFirst:
            startingColumn = 1
            currentColumn = 1

            def highLow = limits[startingColumn]
            int upper = highLow['upper']
            int lower = highLow['lower']

            //start with currentValue as value in the column 0
            def columnValue = arrayValue[currentColumn]

            for (col in 1..<arrayValue.size()) {
                if (columnValue < upper) {
                    next[currentColumn] = columnValue + step
                    break
                }
                else {
                    next[currentColumn++] = lower  //, reset this column, and post increment to the start value point for next column
                    //if we are processing the rows - handle the row column precedence first before handling columns 2...n
                    if (col == 1 && next[0] < limits[0]['upper']) {
                        next[0] = next[0] + step
                        break
                    } else {
                        next[0] = limits[0]['lower']
                    }
                    columnValue =next[currentColumn] //get the start value point for next column
                    highLow = limits[currentColumn]
                    upper = highLow['upper']
                    lower = highLow['lower']
                }
            }
            break

        default:
            next = null
    }

    next
}


List fill = []
//for each entry in each dimension from the start, put start in to begin with
fill << start
def ans = start

// should be 6 entries between start and end - incrementing by columnFirst
for (i in 0..<40) {
    ans = increment (ListFill.byColumnFirst, arrayIndexLimits, ans, 1)
    //reached end of this


    fill << ans

    if (ans == end) {
        println "reached end value column fill first $ans"
        break
    }

    ans
}

println fill

fill = []
fill << start

ans = start
// should be 6 entries between start and end - incrementing by rowFirst
for (i in 0..<40) {
    ans = increment (ListFill.byRowFirst, arrayIndexLimits, ans, 1)
    //reached end of this


    fill << ans

    if (ans == end) {
        println "reached end value row fill first  $ans"
        break
    }

    ans
}

println fill

def decrement = {fillBy, limits, arrayValue, step ->
    ArrayList next = ArrayList.copyOf(arrayValue)
    int startingColumn
    int currentColumn

    switch (fillBy) {
        case ListFill.byColumnFirst:

            startingColumn = 0
            currentColumn = 0

            def highLow = limits[startingColumn]
            int upper = highLow['upper']
            int lower = highLow['lower']

            //start with currentValue as value in the column 0
            def columnValue = arrayValue[currentColumn]

            for (col in 0..<arrayValue.size()) {
                if (columnValue > lower) {
                    next[currentColumn] = columnValue - step
                    break
                }
                else {
                    next[currentColumn++] = upper
                    columnValue =next[currentColumn] //get the start value point for next column

                    highLow = limits[currentColumn]
                    upper = highLow['upper']
                    lower = highLow['lower']
                }
            }
            break

        case ListFill.byRowFirst:
            startingColumn = 1
            currentColumn = 1

            def highLow = limits[startingColumn]
            int upper = highLow['upper']
            int lower = highLow['lower']

            //start with currentValue as value in the column 0
            def columnValue = arrayValue[currentColumn]

            for (col in 1..<arrayValue.size()) {
                if (columnValue > lower) {
                    next[currentColumn] = columnValue - step
                    break
                }
                else {
                    next[currentColumn++] = upper  //, reset this column, and post increment to the start value point for next column
                    //if we are processing the rows - handle the row column precedence first before handling columns 2...n
                    if (col == 1 && next[0] > limits[0]['lower']) {
                        next[0] = next[0] - step
                        break
                    } else {
                        next[0] = limits[0]['upper']
                    }
                    columnValue =next[currentColumn] //get the start value point for next column
                    highLow = limits[currentColumn]
                    upper = highLow['upper']
                    lower = highLow['lower']
                }
            }
            break

        default:
            next = null
    }

    next
}

fill = []
fill << [1,2,1,1]
ans = [1,2,1,1]

arrayIndexLimits =  calcArrayIndex([0,0,0,0], ans)


for (i in 0..<40) {
    ans = decrement (ListFill.byColumnFirst, arrayIndexLimits, ans, 1)
    //reached end of this


    fill << ans

    if (ans == [0,0,0,0]) {
        println "decrement reached bottom value column fill first  $ans"
        break
    }

    ans
}

println fill

fill = []
fill << [1,2,1,1]
ans = [1,2,1,1]

for (i in 0..<40) {
    ans = decrement (ListFill.byRowFirst, arrayIndexLimits, ans, 1)
    //reached end of this


    fill << ans

    if (ans == [0,0,0,0]) {
        println "decrement reached bottom value row fill first  $ans"
        break
    }

    ans
}


println fill
