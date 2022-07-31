plugins {
    kotlin("multiplatform") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"

    id("org.springframework.boot") version "2.6.8"
    id("io.spring.dependency-management") version "1.0.12.RELEASE"
    id("io.kotest.multiplatform") version "5.0.3"
}

group = "dev.springrunner"
version = "1.0"

repositories {
    mavenCentral()
}

dependencyManagement {
    dependencies {
        dependency("org.jetbrains.kotlin:kotlin-reflect:1.6.21")
        dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")

        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.1")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.1")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.1")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")

        dependency("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
        dependency("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
        dependency("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.5")
        dependency("org.jetbrains.kotlinx:kotlinx-html-js:0.7.5")

        dependency("org.jetbrains.kotlin-wrappers:kotlin-react:18.0.0-pre.332-kotlin-1.6.21")
        dependency("org.jetbrains.kotlin-wrappers:kotlin-react-legacy:18.0.0-pre.332-kotlin-1.6.21")
        dependency("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.0.0-pre.332-kotlin-1.6.21")
        dependency("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:6.3.0-pre.332-kotlin-1.6.21")
        dependency("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.0-pre.332-kotlin-1.6.21")
        dependency("org.jetbrains.kotlin-wrappers:kotlin-mui:5.6.2-pre.332-kotlin-1.6.21")

        dependency("io.ktor:ktor-client-core:2.0.3")
        dependency("io.ktor:ktor-client-cio:2.0.3")
        dependency("io.ktor:ktor-client-content-negotiation:2.0.3")
        dependency("io.ktor:ktor-serialization-kotlinx-json:2.0.3")
        dependency("io.ktor:ktor-client-logging:2.0.3")
        dependency("io.ktor:ktor-client-mock:2.0.3")

        dependency("io.github.microutils:kotlin-logging:2.1.23")
        dependency("io.github.microutils:kotlin-logging-jvm:2.1.23")
        dependency("io.github.microutils:kotlin-logging-js:2.1.23")

        dependency("io.kotest:kotest-framework-engine:5.0.3")
        dependency("io.kotest:kotest-assertions-core:5.0.3")
        dependency("io.kotest:kotest-runner-junit5:5.0.3")
        dependency("io.kotest.extensions:kotest-extensions-spring:1.1.1")

        dependency("org.webjars.npm:todomvc-common:1.0.5")
        dependency("org.webjars.npm:todomvc-app-css:2.4.1")
    }
}

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs.plus(listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn"))
                jvmTarget = "11"
            }
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")

                showCauses = true
                showExceptions = true
                showStackTraces = true
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT

                showStandardStreams = false
            }
        }
    }
    js(IR) {
        binaries.executable()
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs.plus("-opt-in=kotlin.RequiresOptIn")
                sourceMap = true
            }
        }
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                outputFileName = "main.js"
                outputPath = File(buildDir, "processedResources/jvm/main/static")
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime")

                implementation("io.ktor:ktor-client-core")
                implementation("io.ktor:ktor-client-content-negotiation")
                implementation("io.ktor:ktor-serialization-kotlinx-json")
                implementation("io.ktor:ktor-client-logging")

                implementation("io.github.microutils:kotlin-logging")

                // Add dependencies to use when writing test codes
                implementation("io.kotest:kotest-framework-engine")
                implementation("io.kotest:kotest-assertions-core")
                implementation("io.ktor:ktor-client-mock")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
                // When configuring IntelliJ project,
                // it is written commonMan area because the dependency reference is not available in commonTest area
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                dependsOn(commonMain)

                implementation("org.jetbrains.kotlin:kotlin-reflect")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

                implementation("org.springframework.boot:spring-boot-starter-validation")

                implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
                implementation("org.springframework:spring-jdbc")
                implementation("io.r2dbc:r2dbc-h2")
                runtimeOnly("com.h2database:h2")

                implementation("org.springframework.boot:spring-boot-starter-webflux")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm")
                runtimeOnly("org.webjars.npm:todomvc-common")
                runtimeOnly("org.webjars.npm:todomvc-app-css")

                runtimeOnly("io.github.microutils:kotlin-logging-jvm")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("io.kotest:kotest-runner-junit5")
                implementation("io.kotest.extensions:kotest-extensions-spring")
            }
        }

        val jsMain by getting {
            dependencies {
                dependsOn(commonMain)

                implementation("org.jetbrains.kotlinx:kotlinx-html-js")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-legacy")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui")

                implementation("io.github.microutils:kotlin-logging-js")

                implementation(npm("uuid", "8.3.2"))
                implementation(npm("is-sorted", "1.0.5"))
            }
        }
        val jsTest by getting
    }
}

tasks {
    named<Copy>("jvmProcessResources") {
        dependsOn(getByName("jsBrowserDevelopmentWebpack"))
    }
}
