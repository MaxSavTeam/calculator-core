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
    maven { url 'https://repo.maxsavteam.com' }
  }
}
```
#### Maven
Add the JitPack repository to your build file:
```xml
<repositories>
  <repository>
      <url>https://repo.maxsavteam.com</url>
  </repository>
</repositories>
```
### Add library dependency
#### Gradle
```groovy
dependencies {
        implementation 'com.maxsavteam:calculator-core:2.0.0'
}
```
#### Maven
```xml
<dependency>
    <groupId>com.maxsavteam</groupId>
    <artifactId>calculator-core</artifactId>
    <version>2.0.0</version>
</dependency>
```
