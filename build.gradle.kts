plugins {
    id("java") // Java support
    alias(libs.plugins.kotlin) // Kotlin support
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
}

val pluginGroup = "com.oakraw"
val pluginVersion = "1.1.4"

group = pluginGroup
version = pluginVersion

// Set the JVM language level used to build the project.
kotlin {
    jvmToolchain(17)
}


repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    val platformType = "IC"
    val platformVersion = "2023.3"

    intellijPlatform {
        create(platformType, platformVersion)
        instrumentationTools()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "MappingCopyPaste"
        version = pluginVersion

        ideaVersion {
            sinceBuild = "233"
            untilBuild = "242.*"
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("SIGN_PASSWORD")
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("8.9").get()
    }
}