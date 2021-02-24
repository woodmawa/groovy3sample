package com.softwood.worksheet

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

ComparableArrayList grid = [
        [0,0], [0,1], [0,2], [0,3],
        [1,0], [1,1], [1,2], [2,3],
        [2,0], [2,1], [2,2], [2,3],
        [3,0], [3,1], [3,2], [3,3]
]

ObjectRange range = new ObjectRange([1,1] as ComparableArrayList, [2,2] as ComparableArrayList)//= 1..5 //[1,1]..[2,2]

List l = range.step(1)

println grid + " with class " +grid.class
println range + " with class " + range.class + " and size " + range.size()