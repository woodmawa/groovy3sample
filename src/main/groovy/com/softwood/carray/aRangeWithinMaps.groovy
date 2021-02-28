package com.softwood.carray

import com.softwood.carray.ComparableArrayList

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

ComparableArrayList ca4 = new ComparableArrayList<>([1,1])
ca4.name = "ca4"
ComparableArrayList ca5 = new ComparableArrayList<>([2,3])
ca4.name = "ca5"

println "\t>> ca 4: "+ ca4
println "\t>> ca 5: " + ca5

def sizeOfCa5  = ca5.size()

ListRange range = new ListRange(ca4, ca5)//= 1..5 //[1,1]..[2,2]



def start = range.from
def end =  range.to

println "\t>> start : $start to end $end"
println range.inspect()

def size = range.size()

def first = range.get(0)
def second = range.get(1)

 iter = range.iterator()

println "\t>> try loop through range "
while (iter.hasNext()){
    println iter.next()
}
println "\t>> loop completed "
//println range.dump()


//List l = range.step(1)

//def iter = range.iterator()

println grid + " with class " +grid.class
println range + "  with class " + range.class + " and size " + range.size()