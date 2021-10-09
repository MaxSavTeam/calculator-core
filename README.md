# calculator-core
Library for solving mathematical expressions and examples.

Supports Java and Android projects

## Dependency
### Add JitPack to your project
#### Gradle  
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
#### Maven
Add the JitPack repository to your build file:
```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>
```
### Add library dependency
#### Gradle
```groovy
dependencies {
        implementation 'com.github.MaxSavTeam:calculator-core:1.7.2'
}
```
#### Maven
```xml
<dependency>
    <groupId>com.github.MaxSavTeam</groupId>
    <artifactId>calculator-core</artifactId>
    <version>1.7.2</version>
</dependency>
```
