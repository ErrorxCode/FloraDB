# FloraDB
A modern scheme less No-Sql database written in java. World's 1st static database that is all about just one utility class. This database is purely written with java 16 SE and does not include any framwork or library classes. So you can use this on platform of java application.


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

## It's easy
```java
FloraDB.init().crud().commit();
```

## Contribution
Contribution are always open. Please make a issue regarding any bug or feature request.
