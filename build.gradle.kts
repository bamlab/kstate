import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform") version "1.3.72"
    `maven-publish`
    id("org.jetbrains.dokka") version "0.10.1"
    signing
}

group = "com.github.tpucci"
version = "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }
    }
}

afterEvaluate {
    extensions.findByType<PublishingExtension>()?.apply {
        repositories {
            maven {
                url = uri(
                    if (isReleaseBuild) {
                        "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                    } else {
                        "https://oss.sonatype.org/content/repositories/snapshots"
                    }
                )
                credentials {
                    username = properties["sonatypeUsername"].toString()
                    password = properties["sonatypePassword"].toString()
                }
            }
        }

        publications.withType<MavenPublication>().configureEach {
            artifact(emptyJavadocJar.get())

            pom {
                name.set("kstate")
                description.set("A Kotlin Multiplatform library for creating state machines")
                url.set("https://github.com/tpucci/kstate")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("tpucci")
                        name.set("Thomas Pucci")
                    }
                }
                scm {
                    url.set("https://github.com/tpucci/kstate")
                    connection.set("scm:git:https://github.com/tpucci/kstate")
                }
            }
        }

        tasks.withType<Sign>().configureEach {
            onlyIf { isReleaseBuild }
        }

        extensions.findByType<SigningExtension>()?.apply {
            val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
            val key = properties["signingKey"]?.toString()?.replace("\\n", "\n")
            val password = properties["signingPassword"]?.toString()

            useInMemoryPgpKeys(key, password)
            sign(publishing.publications)
        }
    }

    tasks.withType(DokkaTask::class.java) {
        multiplatform {
            extensions.findByType<KotlinMultiplatformExtension>()?.targets?.forEach { create(it.name) }
        }
    }
}


val isReleaseBuild: Boolean
    get() = (properties["isReleaseBuild"] == "true")
