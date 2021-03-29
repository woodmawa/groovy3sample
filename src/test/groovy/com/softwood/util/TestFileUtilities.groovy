package com.softwood.util

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class TestFileUtilities extends Specification {

    def "test file with resources: " () {

        given:
        File projF = FileUtilities.getFileForResource("resource:testDataFile.csv")
        File testF = FileUtilities.getFileForResource("testResource:test-testDataFile.csv")
        String canPath = testF.getCanonicalPath()
        String projCanonicalPath = projF.getCanonicalPath()

        Path projPath = FileUtilities.getPathForResource("resource:testDataFile.csv")

        expect:
        projF.exists()
        testF.exists()
        Files.exists(projPath)

    }

    def "test paths list " () {
        given :

        List<String> paths = FileUtilities.getPathList("classpath:testDataFile.csv")

        expect:
        paths.size () == 2
    }
}
