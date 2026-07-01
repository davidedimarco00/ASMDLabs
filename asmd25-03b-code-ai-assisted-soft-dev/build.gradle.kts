plugins {
    java
}

group = "it.unibo"
version = "0.1.0-SNAPSHOT"

// Java Toolchain configuration
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        // Optional: specify vendor (e.g., ADOPTIUM, AMAZON, ORACLE)
        // vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // LangChain4j - AI Framework
    implementation("dev.langchain4j:langchain4j:1.11.0")
    implementation("dev.langchain4j:langchain4j-ollama:1.11.0")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:1.11.0")

    // Smile - Machine Learning (Java Core)
    implementation("com.github.haifengl:smile-core:4.3.0")
    implementation("com.github.haifengl:smile-plot:4.3.0")

    // JSON Processing
    implementation("com.google.code.gson:gson:2.13.2")

    // Testing - Fixed with JUnit BOM for stability
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}