plugins {
    kotlin("multiplatform") version "1.3.72"
    `maven-publish`
    signing
}

group = "com.github.tpucci"
version = "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
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

    configure(listOf(targets["metadata"], jvm())) {
        mavenPublication {
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
    }
}

publishing {
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

val isReleaseBuild: Boolean
    get() = (properties["isReleaseBuild"] == "true")

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}