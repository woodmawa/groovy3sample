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

//put least significant entry first [2,1]
def leastSigEntriesFirst = end.reverse()
for (i in 0..<dimensions) {
    ArrayList peekValue, intermediate
    int upper = end[i-1]//leastSigEntriesFirst[i]
    int lower = start[i-1]

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
