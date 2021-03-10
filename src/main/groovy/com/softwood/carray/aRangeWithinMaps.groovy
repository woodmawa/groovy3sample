package com.softwood.carray

import com.softwood.carray.ComparableArrayList

List l = [1,1,1] - [2,2,2]

ComparableArrayList ca1 = [1,2]
ComparableArrayList ca2 = [1,2,3]
ComparableArrayList ca3 = [1,2,4]

int res =  ca1.compareTo(ca2)
int res2 = ca2.compareTo(ca1)
assert res == -1
assert res2 == 1

assert ca1.compareTo(ca1) == 0
assert ca2.compareTo(ca3) == -1

def iter = ca1.iterator()
println "\t>> ca1 elements : " + ca1.elements

ComparableArrayList grid = [
        [0,0], [0,1], [0,2], [0,3],
        [1,0], [1,1], [1,2], [2,3],
        [2,0], [2,1], [2,2], [2,3],
        [3,0], [3,1], [3,2], [3,3]
]

grid.name = "grid"
println "\t>> grid elements : " + grid.elements

def sizeOfGrid = grid.size()


ComparableArrayList ca4 = new ComparableArrayList<>([0,0,0])
ca4.name = "ca4"
ComparableArrayList ca5 = new ComparableArrayList<>([1,1,1])
ca5.name = "ca5"

println "\t>> ca 4: "+ ca4
println "\t>> ca 5: " + ca5

def sizeOfCa5  = ca5.size()

ListRange lr = new ListRange (new ComparableArrayList([0,0]), new ComparableArrayList([1,1]) )
Iterator lri = lr.iterator()

print "\t>> try loop through range [0,0]..[1,1], should see 4 entries \n\t\t"
while (lri.hasNext()){
    def entry = lri.next()
    print "$entry"
}
println "\n\t>> loop completed "

ListRange range = new ListRange(ca4, ca5)
ListRange revRange = new ListRange(ca4, ca5, true)

ListRange revSublist = revRange.subList(2,4)
println "\t>> revSublist : ${revSublist.toString()} + rev:$revSublist.reverse, from:$revSublist.from, to:$revSublist.to"

ListRange sublist = range.subList(2,4)
println "\t>> sublist : ${sublist.toString()} + rev:$sublist.reverse, from:$sublist.from, to:$sublist.to"


def start = range.from
def end =  range.to

println "\t>> start : $start to end $end"
println "\t>> inspect() : "+ range.inspect()

def size = range.size()

def first = range.get(0)
assert first == [0,0,0]
def second = range.get(1)  //row first by default
assert second == [0,1,0]
def revFirst = range[-2]
assert revFirst == [1,0,1]

 iter = range.iterator()

print "\t>> try loop through range \n\t\t"
while (iter.hasNext()){
    print "${iter.next()}"
}
println "\n\t>> loop completed "

println "\t>> grid toString(): " + grid.toString()
println "\t>> grid: " + grid        //doesnt invoke tostring - semms to calll ? to get a dump
//println "\t>> grid dump(): " + grid.dump()  //causes reflection warning

print "\t>> range : " +range
println "  with class " + range.class + " and size " + range.size()