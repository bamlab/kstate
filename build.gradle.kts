plugins {
    kotlin("multiplatform") version "1.3.72"
    `maven-publish`
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
            val targetPublication = this@mavenPublication
            tasks.withType<AbstractPublishToMaven>()
                    .matching { it.publication == targetPublication }
                    .all { onlyIf { findProperty("isMainHost") == "true" } }

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
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            credentials {
                username = properties["sonatypeUsername"].toString()
                password = properties["sonatypePassword"].toString()
            }
        }
    }

    extensions.findByType<SigningExtension>()?.apply {
        val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
        val key = properties["signingKeyId"]?.toString()?.replace("\\n", "\n")
        val password = properties["signingPassword"]?.toString()

        useInMemoryPgpKeys(key, password)
        sign(publishing.publications)
    }
}
