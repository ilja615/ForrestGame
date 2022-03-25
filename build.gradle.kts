/*
 * Copyright (c) 2022 the ForrestGame contributors.
 *
 * This file is part of ForrestGame.
 *
 * ForrestGame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ForrestGame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ForrestGame.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
}

group = "com.github.ilja615"
base.archivesName.set("forrestgame")
version = "0.1.0"

@Suppress("INACCESSIBLE_TYPE")
val lwjglNatives = when (OperatingSystem.current()) {
    OperatingSystem.WINDOWS -> "natives-windows"
    OperatingSystem.MAC_OS ->
        if (System.getProperty("os.arch").startsWith("aarch64")) "natives-macos-arm64"
        else "natives-macos"
    OperatingSystem.LINUX -> "natives-linux"
    else -> throw Error("Unrecognized or unsupported operating system. Please set \"lwjglNatives\" manually.")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.2")) // testing
    testImplementation("org.junit.jupiter:junit-jupiter") // testing
    implementation("ch.qos.logback:logback-classic:1.2.11") // logging
    implementation("com.google.guava:guava:31.1-jre") // collections
    compileOnly("org.jetbrains:annotations:23.0.0") // annotations
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.1")) // lwjgl
    implementation("org.joml:joml:1.10.4") // joml
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
    implementation(group = "org.lwjgl", name = "lwjgl-meshoptimizer")
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
    implementation(group = "org.lwjgl", name = "lwjgl-spvc")
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
    runtimeOnly(group = "org.lwjgl", name = "lwjgl", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-bgfx", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-jemalloc", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-libdivide", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-llvm", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-lmdb", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-lz4", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-meow", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-meshoptimizer", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-nanovg", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-nfd", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-nuklear", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-opengles", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-openvr", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-opus", classifier = lwjglNatives)
    if (lwjglNatives == "natives-windows") {
        runtimeOnly(group = "org.lwjgl", name = "lwjgl-ovr", classifier = lwjglNatives)
    }
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-par", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-remotery", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-rpmalloc", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-shaderc", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-spvc", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-sse", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-stb", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-tinyexr", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-tinyfd", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-tootle", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-vma", classifier = lwjglNatives)
    if (lwjglNatives == "natives-macos" || lwjglNatives == "natives-macos-arm64") {
        runtimeOnly(group = "org.lwjgl", name = "lwjgl-vulkan", classifier = lwjglNatives)
    }
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-xxhash", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-yoga", classifier = lwjglNatives)
    runtimeOnly(group = "org.lwjgl", name = "lwjgl-zstd", classifier = lwjglNatives)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("com.github.ilja615.forrestgame.ForrestGame")

    if (OperatingSystem.current().isMacOsX) applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

tasks {
    test {
        useJUnitPlatform()
    }
}