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

plugins {
    java
    application
}

group = "com.github.ilja615"
base.archivesName.set("ForrestGame")
version = "0.1.0"

val lwjglNatives = (System.getProperty("os.name")!! to System.getProperty("os.arch")!!).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } -> "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) } -> "natives-windows"
        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.2")) // JUnit bill of materials
    testImplementation("org.junit.jupiter:junit-jupiter") // JUnit Jupiter (unit testing)
    implementation("ch.qos.logback:logback-classic:1.2.11") // Logback (logging)
    implementation("com.google.guava:guava:31.1-jre") // Guava ("core libraries for Java")
    compileOnly("org.jetbrains:annotations:23.0.0") // JetBrains annotations
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.1")) // LWJGL bill of materials
    implementation("org.lwjgl", "lwjgl") // LWJGL
    implementation("org.lwjgl", "lwjgl-glfw") // GLFW (a lot of stuff)
    implementation("org.lwjgl", "lwjgl-openal") // OpenAL ("a cross-platform 3D audio API")
    implementation("org.lwjgl", "lwjgl-opengl") // OpenGL ("most widely adopted 2D and 3D graphics API in the industry")
    implementation("org.lwjgl", "lwjgl-stb") // stb ("single-file public domain libraries for C/C++")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("com.github.ilja615.forrestgame.ForrestGame")

    if (lwjglNatives.startsWith("natives-macos")) applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}

tasks {
    jar {
        manifest {
            attributes(
                "Manifest-Version" to "1.0",
                "Main-Class" to "com.github.ilja615.forrestgame.ForrestGame"
            )
        }
    }

    test {
        useJUnitPlatform()
    }
}