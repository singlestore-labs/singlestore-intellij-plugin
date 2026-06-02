plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.21"
    id("org.jetbrains.intellij.platform") version "2.10.2"
}

group = "com.singlestore.intellij"
version = "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation(kotlin("test"))

    intellijPlatform {
        intellijIdeaCommunity("2024.3.6", useInstaller = false)
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }
}

kotlin {
    jvmToolchain(17)
}

tasks {
    test {
        useJUnitPlatform()
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "com.singlestore.intellij.plugin"
        name = "SingleStore IntelliJ Plugin"
        version = project.version.toString()
        description = "SingleStore language server client for IntelliJ IDEA."
        ideaVersion {
            sinceBuild = "243"
        }
        vendor {
            name = "SingleStore Labs"
            url = "https://github.com/singlestore-labs"
        }
    }
}
