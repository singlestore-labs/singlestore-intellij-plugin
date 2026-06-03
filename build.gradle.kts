plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.21"
    id("org.jetbrains.intellij.platform") version "2.10.2"
}

group = "com.singlestore.client"
version = (findProperty("version") as? String)?.takeIf { it != "unspecified" } ?: "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testRuntimeOnly("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdea("2024.3.6") {
            useInstaller = false
        }
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }
}

kotlin {
    jvmToolchain(17)
}

tasks {
    val generateVersionProperties by registering {
        val outputDir = layout.buildDirectory.dir("generated/resources")
        val pluginVersion = version.toString()
        inputs.property("pluginVersion", pluginVersion)
        outputs.dir(outputDir)
        doLast {
            val file = outputDir.get().file("version.properties").asFile
            file.parentFile.mkdirs()
            file.writeText("plugin.version=$pluginVersion\n")
        }
    }

    processResources {
        dependsOn(generateVersionProperties)
        from(generateVersionProperties.map { it.outputs.files })
    }

    runIde {
        if (project.hasProperty("debugPlugin")) {
            jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
        }
    }

    test {
        useJUnitPlatform()
        jvmArgs("-Djava.awt.headless=true")
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "com.singlestore.client.plugin"
        name = "SingleStore Language Server Client"
        version = project.version.toString()
        description = "SingleStore language server client for JetBrains IDEs."
        ideaVersion {
            sinceBuild = "243"
        }
        vendor {
            name = "SingleStore Labs"
            url = "https://github.com/singlestore-labs"
        }
    }
    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}
