plugins {
    java
    id("io.izzel.taboolib") version "1.31"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    install("common")
    install("common-5")
    install("module-configuration")
    install("module-chat")
    install("module-effect")
    install("module-lang")
    install("module-kether")
    install("module-database")
    install("module-nms")
    install("module-nms-util")
    install("platform-bukkit")
    install("expansion-command-helper", "expansion-player-database")
    classifier = null
    version = "6.0.3-21"

    description {
        contributors {
            name("Lanscarlos")
        }
        dependencies {
            name("Zaphkiel")
            name("PlaceholderAPI").optional(true)
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("ink.ptms:Zaphkiel:1.7.4")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}