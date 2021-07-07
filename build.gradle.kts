import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    kotlin("multiplatform") version "1.3.72"
    `maven-publish`
    id("org.jetbrains.dokka") version "1.4.10.2"
    id("io.codearte.nexus-staging") version "0.22.0"
    signing
}

group = "tech.bam.kstate"
version = "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk-common:1.12.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
                implementation("io.mockk:mockk:1.12.0")
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
                url.set("https://github.com/bamlab/kstate")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("bamlab")
                        name.set("Thomas Pucci")
                    }
                }
                scm {
                    url.set("https://github.com/bamlab/kstate")
                    connection.set("scm:git:https://github.com/bamlab/kstate")
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
}

nexusStaging {
    packageGroup = "tech.bam"
    username = properties["sonatypeUsername"].toString()
    password = properties["sonatypePassword"].toString()
}


val isReleaseBuild: Boolean
    get() = (properties["isReleaseBuild"] == "true")

tasks.withType<DokkaTask>().configureEach {
    outputDirectory.set(buildDir.resolve("dokka"))

    dokkaSourceSets {
        named("commonMain") {
            samples.from("README.md")
            includes.from("index.md")
            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(URL("https://github.com/bamlab/api/kstate/blob/master/src/commonMain/kotlin"))
                remoteLineSuffix.set("#L")
            }
            // externalDocumentationLink {
            //     url.set(URL("https://bamlab.github.io/kstate/api/kstate/"))
            // }
        }
    }
}
