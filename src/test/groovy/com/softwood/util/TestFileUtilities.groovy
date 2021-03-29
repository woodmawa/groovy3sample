package com.softwood.util

import spock.lang.Specification

class TestFileUtilities extends Specification {

    def "test file with resources: " () {

        given:
        File projF = FileUtilities.getFileForResource("resource:testDataFile.csv")
        File testF = FileUtilities.getFileForResource("testResource:test-testDataFile.csv")
        String canPath = testF.getCanonicalPath()
        String projCanonicalPath = projF.getCanonicalPath()

        expect:
        projF.exists()
        testF.exists()

    }
}
