package scripts

import groovy.json.*

//requires additional maven import to get this

def jsonSlurper = new JsonSlurper()
def object = jsonSlurper.parseText '''
    { "simple": 123,
      "fraction": 123.66,
      "exponential": 123e12
    }'''

assert object instanceof Map
assert object.simple.class == Integer
assert object.fraction.class == BigDecimal
assert object.exponential.class == BigDecimal

jsonSlurper = new JsonSlurper()
object = jsonSlurper.parseText('{ "myList": [4, 8, 15, 16, 23, 42] }')

assert object instanceof Map
assert object.myList instanceof List
assert object.myList == [4, 8, 15, 16, 23, 42]

class Person {
    String name
    String title
    int age
    String password
    Date dob
    URL favoriteUrl
}

Person person = new Person(name: 'John', title: null, age: 21, password: 'secret',
        dob: Date.parse('yyyy-MM-dd', '1984-12-15'),
        favoriteUrl: new URL('http://groovy-lang.org/'))

def generator = new JsonGenerator.Options()
        .excludeNulls()
        .dateFormat('yyyy@MM')
        .excludeFieldsByName('age', 'password')
        .excludeFieldsByType(URL)
        .build()

assert generator.toJson(person) == '{"dob":"1984@12","name":"John"}'

generator = new JsonGenerator.Options()
        .addConverter(URL) { URL u, String key ->
            if (key == 'favoriteUrl') {
                u.getHost()
            } else {
                u
            }
        }
        .build()

assert generator.toJson(person) == '{"favoriteUrl":"groovy-lang.org","name":"John"}'

// No key available when generating a JSON Array
def list = [new URL('http://groovy-lang.org/json.html#_jsonoutput')]
assert generator.toJson(list) == '["http://groovy-lang.org/json.html#_jsonoutput"]'

// First parameter to the converter must match the type for which it is registered
shouldFail(IllegalArgumentException) {
    new JsonGenerator.Options()
            .addConverter(Date) { Calendar cal -> }
}