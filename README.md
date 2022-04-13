# [MIGRATED] FloraDB
<p align="left">
  <a href="#"><img alt="Version" src="https://img.shields.io/badge/Language-Java-1DA1F2?style=flat-square&logo=java"></a>
  <a href="#"><img alt="Bot" src="https://img.shields.io/badge/Version-1.0-green"></a>
  <a href="https://www.instagram.com/x__coder__x/"><img alt="Instagram - x__coder__" src="https://img.shields.io/badge/Instagram-x____coder____x-lightgrey"></a>
  <a href="#"><img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/ErrorxCode/OTP-Verification-Api?style=social"></a>
  </p>

A modern scheme less No-Sql database written in java. World's 1st static database that is all about just one utility class. This database is purely written with java 16 SE and does not include any framwork or library classes. So you can use this on platform of java application.

## 🎊🎊 Now FloraDB is migrated to [CloremDB](https://github.com/ErrorxCode/CloremDB)
Now FloraDB is a part of [Clorabase](https://clorabase.tk). Please use this database from **CloremDB** as this project is going to shut-down soon.

## Features
- Easy, simple and lightweight
- Supports all major datatypes
- Scheme less & linear data structure
- Advance querying engine
- Plantform independent


## Implimentation
#### Gradle

Add it in your root (Project) build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```
Add the dependency in your app build.gradle (Module):
```groovy
	dependencies {
	        implementation 'com.github.ErrorxCode:FloraDB:Tag'
	}
```

#### Maven
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Add the dependency:
```xml
	<dependency>
	    <groupId>com.github.ErrorxCode</groupId>
	    <artifactId>FloraDB</artifactId>
	    <version>Tag</version>
	</dependency>
```

## Acknowledgement 
- [What is no-sql](https://en.wikipedia.org/wiki/NoSQL)
- [Realm.io](https://realm.io/)
- [Room](https://developer.android.com/training/data-storage/room)

## Documentation
- [Javadocs](https://errorxcode.github.io/docs/floradb/index.html)
- [Guide](https://github.com/ErrorxCode/FloraDB/wiki/Documentation)

## It's easy
```java
FloraDB.init().crud().commit();
```

## Contribution
Contribution are always open. Please make a issue regarding any bug or feature request.
