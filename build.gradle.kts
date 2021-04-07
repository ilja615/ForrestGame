import org.gradle.internal.os.OperatingSystem

plugins {
    eclipse
    idea
    java
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("net.minecrell.licenser") version "0.4.1"
    id("com.github.ben-manes.versions") version "0.38.0"
}

group = "com.github.ilja615"
base.archivesBaseName = "forrestgame"
version = "1.0.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    fun property(name: String) = project.property(name)!!.toString()

    // testing
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-api", version = property("jupiterVersion"))
    testRuntimeOnly(group = "org.junit.jupiter", name = "junit-jupiter-engine")
    implementation(group = "com.google.guava", name = "guava", version = property("guavaVersion"))

    // annotations
    compileOnly(group = "org.jetbrains", name = "annotations", version = property("jetbrainsAnnotationsVersion"))

    // logging
    implementation(group = "org.slf4j", name = "slf4j-api", version = property("slf4jVersion"))
    implementation(group = "ch.qos.logback", name = "logback-core", version = property("logbackVersion"))
    implementation(group = "ch.qos.logback", name = "logback-classic", version = property("logbackVersion"))

    // lwjgl and joml
    implementation(platform("org.lwjgl:lwjgl-bom:${property("lwjglVersion")}"))
    implementation(group = "org.lwjgl", name = "lwjgl")
    implementation(group = "org.lwjgl", name = "lwjgl-assimp")
    implementation(group = "org.lwjgl", name = "lwjgl-bgfx")
    implementation(group = "org.lwjgl", name = "lwjgl-cuda")
    implementation(group = "org.lwjgl", name = "lwjgl-egl")
    implementation(group = "org.lwjgl", name = "lwjgl-glfw")
    implementation(group = "org.lwjgl", name = "lwjgl-jawt")
    implementation(group = "org.lwjgl", name = "lwjgl-jemalloc")
    implementation(group = "org.lwjgl", name = "lwjgl-libdivide")
    implementation(group = "org.lwjgl", name = "lwjgl-llvm")
    implementation(group = "org.lwjgl", name = "lwjgl-lmdb")
    implementation(group = "org.lwjgl", name = "lwjgl-lz4")
    implementation(group = "org.lwjgl", name = "lwjgl-meow")
    implementation(group = "org.lwjgl", name = "lwjgl-nanovg")
    implementation(group = "org.lwjgl", name = "lwjgl-nfd")
    implementation(group = "org.lwjgl", name = "lwjgl-nuklear")
    implementation(group = "org.lwjgl", name = "lwjgl-odbc")
    implementation(group = "org.lwjgl", name = "lwjgl-openal")
    implementation(group = "org.lwjgl", name = "lwjgl-opencl")
    implementation(group = "org.lwjgl", name = "lwjgl-opengl")
    implementation(group = "org.lwjgl", name = "lwjgl-opengles")
    implementation(group = "org.lwjgl", name = "lwjgl-openvr")
    implementation(group = "org.lwjgl", name = "lwjgl-opus")
    implementation(group = "org.lwjgl", name = "lwjgl-ovr")
    implementation(group = "org.lwjgl", name = "lwjgl-par")
    implementation(group = "org.lwjgl", name = "lwjgl-remotery")
    implementation(group = "org.lwjgl", name = "lwjgl-rpmalloc")
    implementation(group = "org.lwjgl", name = "lwjgl-shaderc")
    implementation(group = "org.lwjgl", name = "lwjgl-sse")
    implementation(group = "org.lwjgl", name = "lwjgl-stb")
    implementation(group = "org.lwjgl", name = "lwjgl-tinyexr")
    implementation(group = "org.lwjgl", name = "lwjgl-tinyfd")
    implementation(group = "org.lwjgl", name = "lwjgl-tootle")
    implementation(group = "org.lwjgl", name = "lwjgl-vma")
    implementation(group = "org.lwjgl", name = "lwjgl-vulkan")
    implementation(group = "org.lwjgl", name = "lwjgl-xxhash")
    implementation(group = "org.lwjgl", name = "lwjgl-yoga")
    implementation(group = "org.lwjgl", name = "lwjgl-zstd")

    @Suppress("INACCESSIBLE_TYPE")
    val natives = when (OperatingSystem.current()) {
        OperatingSystem.LINUX -> "natives-linux"
        OperatingSystem.MAC_OS -> "natives-macos"
        OperatingSystem.WINDOWS -> "natives-windows"
        else -> error("Your OS is not supported.")
    }

    runtimeOnly(group = "org.lwjgl", name = "lwjgl", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-assimp", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-bgfx", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-glfw", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-jemalloc", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-libdivide", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-llvm", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-lmdb", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-lz4", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-meow", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-nanovg", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-nfd", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-nuklear", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-openal", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-opengl", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-opengles", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-openvr", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-opus", classifier = natives)
    if (natives == "natives-windows") runtimeOnly(group = "org.lwjgl", name = "lwjgl-ovr", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-par", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-remotery", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-rpmalloc", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-shaderc", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-sse", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-stb", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-tinyexr", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-tinyfd", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-tootle", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-vma", classifier = natives)
    if (natives == "natives-macos") runtimeOnly(group = "org.lwjgl", name = "lwjgl-vulkan", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-xxhash", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-yoga", classifier = natives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-zstd", classifier = natives)

    implementation(group = "org.joml", name = "joml", version = property("jomlVersion"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(15))
    }
}

application {
    @Suppress("DEPRECATION") // required for shadow
    mainClassName = "com.github.ilja615.forrestgame.ForrestGame"

    if (OperatingSystem.current().isMacOsX) applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

license {
    header = rootProject.file("LICENSE_HEADER.txt")

    ext {
        this["name"] = "ilja615"
        this["years"] = "2021"
        this["projectName"] = "Forrest Game"
    }

    include("**/*.java")
}

tasks {
    dependencyUpdates {
        gradleReleaseChannel = "current"
        outputFormatter = "html"

        rejectVersionIf { candidate.version.contains("[.-]alpha|[.-]beta|[.-]rc\\d|[.-]m\\d".toRegex(RegexOption.IGNORE_CASE)) }
    }

    test {
        useJUnitPlatform()
    }
}
