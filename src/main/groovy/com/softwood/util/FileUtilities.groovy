package com.softwood.util

import java.nio.file.Path
import java.nio.file.Paths

/**
 * start of file resolution using classpath or resource
 *
 *
 */
class FileUtilities {

    static List getPathList (String filename) {
        filename.replaceAll("\\/", File.separator)
        String projectRoot = System.getProperty("user.dir")
        def split = projectRoot.split("src")
        projectRoot = split?[0]
        def hasClassPath = filename.trim().startsWith("classpath:")
        def hasTestClassPath = filename.trim().startsWith("testClasspath:")

        List paths = []
        String cleanedFile = (filename.trim() - "classpath:").trim()
        if (hasClassPath) {
            if (new File (projectRoot.concat("build${File.separator}resources${File.separator}main")).exists() )
                paths << projectRoot.concat("build${File.separator}resources${File.separator}main${File.separator}$cleanedFile")
            if (new File (projectRoot.concat("build${File.separator}classes${File.separator}groovy")).exists() )
                paths << projectRoot.concat("build${File.separator}classes${File.separator}groovy${File.separator}main${File.separator}$cleanedFile")
            if (new File (projectRoot.concat("build${File.separator}classes${File.separator}java")).exists() )
                paths << projectRoot.concat("build${File.separator}classes${File.separator}java${File.separator}main${File.separator}$cleanedFile")

        } else if (hasTestClassPath) {
            if (new File (projectRoot.concat("build${File.separator}resources${File.separator}main")).exists() )
                paths << projectRoot.concat("build${File.separator}resources${File.separator}main${File.separator}$cleanedFile")
            if (new File (projectRoot.concat("build${File.separator}classes${File.separator}groovy")).exists() )
                paths << projectRoot.concat("build${File.separator}classes${File.separator}groovy${File.separator}test${File.separator}$cleanedFile")
            if (new File (projectRoot.concat("build${File.separator}classes${File.separator}java")).exists() )
                paths << projectRoot.concat("build${File.separator}classes${File.separator}java${File.separator}test${File.separator}$cleanedFile")


        }
        else {
            cleanedFile = filename.trim()
            paths << System.getProperty("user.dir")+File.separator+cleanedFile
        }
        paths
    }

    static File getFileForResource (String filename){
        filename.replaceAll("\\/", File.separator)
        String projectRoot = System.getProperty("user.dir")
        def split = projectRoot.split("${File.separator}src")
        projectRoot = split?[0]
        def hasClassPath = filename.trim().startsWith("resource:")
        def hasTestClassPath = filename.trim().startsWith("testResource:")

        String cleanedFile = ""
        if (hasClassPath) {
            cleanedFile = (filename.trim() - "resource:").trim()
            String name = projectRoot.concat("${File.separator}build${File.separator}resources${File.separator}main${File.separator}${cleanedFile}")
            new File(name)
        } else if (hasTestClassPath) {
            cleanedFile = (filename.trim() - "testResource:").trim()
            String name = projectRoot.concat("${File.separator}build${File.separator}resources${File.separator}test${File.separator}${cleanedFile}")
            new File(name)

        } else {
            cleanedFile = filename.trim()
            new File(cleanedFile)
        }
    }

    static Path getPathForResource (String filename) {
        filename.replaceAll("\\/", File.separator)
        String projectRoot = System.getProperty("user.dir")
        def split = projectRoot.split("${File.separator}src")
        projectRoot = split?[0]
        def hasClassPath = filename.trim().startsWith("resource:")
        def hasTestClassPath = filename.trim().startsWith("testResource:")

        String cleanedFile = ""
        if (hasClassPath) {
            cleanedFile = (filename.trim() - "resource:").trim()
            String name = projectRoot.concat("${File.separator}build${File.separator}resources${File.separator}main${File.separator}${cleanedFile}")
            Paths.get(name )
        } else if (hasTestClassPath) {
            cleanedFile = (filename.trim() - "testResource:").trim()
            String name = projectRoot.concat("${File.separator}build${File.separator}resources${File.separator}test${File.separator}${cleanedFile}")
            Paths.get (name)

        }else {
            cleanedFile = filename.trim()
            Paths.get (cleanedFile)
        }

    }
}
