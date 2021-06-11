import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `java-library`
    `maven-publish`
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.checkerframework") version "0.5.21"
}

group = "net.md_5"
version = "2.0"

repositories {
    maven("https://nexus.proximy.st/repository/maven-public/")

    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("net.sf.jopt-simple:jopt-simple:5.0.4")
    implementation("org.apache.bcel:bcel:6.5.0")
    implementation("org.ow2.asm:asm:9.1")
    implementation("org.ow2.asm:asm-commons:9.1")
}

application {
    mainClass.set("net.md_5.ss.SpecialSource")
    @Suppress("DEPRECATION") // Shadow requirement
    mainClassName = mainClass.get()
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to application.mainClass.get())
        }
    }

    named("build") {
        dependsOn(withType<ShadowJar>())
    }
}