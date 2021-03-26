# calculator-core

## Dependency
### Gradle
#### Add JitPack to your project
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
#### Add library dependency
```groovy
dependencies {
        implementation 'com.github.MaxSavTeam:calculator-core:1.6.1'
}
```

### Maven
#### Add JitPack to your project
Add it in your root build.gradle at the end of repositories:
```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>
```
#### Add library dependency
```xml
<dependency>
    <groupId>com.github.MaxSavTeam</groupId>
    <artifactId>calculator-core</artifactId>
    <version>1.6.1</version>
</dependency>
```
