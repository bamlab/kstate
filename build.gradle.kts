plugins {
    kotlin("multiplatform") version "1.3.72"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.5"
}

group = "com.github.tpucci"
version = "0.0.0-development"

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
        }
    }
}

bintray {
    user = System.getProperty("BINTRAY_USER")
    key = System.getProperty("BINTRAY_API_KEY")
    publish = true
    setPublications("jvm")

    pkg.apply {
        repo = "kstate"
        name = "kstate"
        userOrg = "tpucci"
        setLicenses("MIT")
        vcsUrl = "https://github.com/tpucci/kstate.git"
        version.apply {
            name = rootProject.version.toString()
            desc = rootProject.version.toString()
            vcsTag = rootProject.version.toString()
        }
    }

}