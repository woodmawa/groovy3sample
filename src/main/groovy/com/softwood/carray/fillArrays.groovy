package com.softwood.carray

def start = [0,0]
def end = [1,2]

def byColumnFirst = [[0,0],[1,0],[0,1][1,1]]
def byRowFirst = [[0,0],[0,1],[0,2],[1,0][1,1],[1,2]]

def dimensions = end.size()
def entries = byRowFirst.size()

enum ListFill {
    byRowFirst,
    byColumnFirst
}

ListFill approach = ListFill.byRowFirst

List fill = []
//try by row first

def value = start
def numColumns = end[0] - start[0]

HashMap barrelIndex =  [:]
for (i in 0..<dimensions){
    //start with x, y, z
    def upper = end[i]
    def lower = start[i]
    def range = [:]  //java.util.LinkedHashMap
    range << [upper: upper]
    range << [lower: lower]
    barrelIndex << [(i): range]
}

println barrelIndex  //this is map of dimension position and lower and upper bound for each position

//for each entry in each dimension from the start
fill << start

for (i in 0..<dimensions) {
    def highLow = barrelIndex[i]
    int upper = highLow['upper']
    int lower = highLow['lower']

    increment (i, value, 1)
    //reached end of this
    value
}

Closure increment (barrelNumber, value, step) {
    def highLow = barrelIndex[barrelNumber]
    int upper = highLow['upper']
    int lower = highLow['lower']

    def intermediate = []
    for (i in lower..upper) {
        intermediate = [value[i]+1, value[1]]
        value = intermediate

    }
}

//put least significant entry first [2,1]
def leastSigEntriesFirst = end.reverse()
for (i in 0..<dimensions) {
    ArrayList peekValue, intermediate
    int upper = end[-1 -i]//leastSigEntriesFirst[i]
    int lower = start[-1 -i]

    fill << value

    for (j in lower..<upper) {
        intermediate = [value[j]+1]
        for (others in (i+1)..<dimensions){
            intermediate.add (value.reverse()[others])
        }
        peekValue = intermediate.reverse()
        fill << peekValue
        value = peekValue
    }
    value = [i+1, lower]
}

for (i in 0..<numColumns) {
    int j = end.size()-1
    int upperRange = end[j]
    int lowerRange = start[j]

    for (e in 0..<upperRange) {
        if (value[j] + 1 <= end[j])
            fill << (value = [start[i], value[j] + 1])
        else if (value[i] + 1 <= end[i])
            fill << (value = [value[i] + 1, start[j]])
    }
}

println fill
