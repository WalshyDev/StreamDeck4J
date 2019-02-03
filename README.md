# StreamDeck4J
The StreamDeck library for Java!

## Usage
Create an instance of `StreamDeck`, add any listeners you may want (You can either use `EventListener` or the more preferred `ListenerAdapter` for this). Finally, just call `connect()`!

### Example code
For a **full** example check our the `src/main/test/java/Example` class.
```java
public static void main(String[] args) {
    StreamDeck4J streamDeck4J = new StreamDeck4J();
    streamDeck4J.addListener(new AbstractListener() {
        @Override
        public void onActionAppeared(ActionAppearedEvent event) {
            streamDeck4J.setTitle(
                event.getContext(),
                "Made With\nStreamDeck4J!",
                Destination.HARDWARE_AND_SOFTWARE
            );
        }
    });
    streamDeck4J.connect(args);
}
```

**I recommend adding a logger in, slf4j-api is a dependency and logging is setup. You can add your on implementation. Logback-classic is a nice one, example logback.xml [here](https://gist.github.com/WalshyDev/dfcd1f155b71c68bf596deb44bf6e15f)**

## Building for the StreamDeck
1. Compile your jar with your build tool
2. Use Launch4J (http://launch4j.sourceforge.net) to make it an exe
3. Put the exe in your `manifest.json` as the `CodePath`
4. Profit!

## How to add?
### Maven
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.WalshyDev</groupId>
    <artifactId>StreamDeck4J</artifactId>
    <version>$RELEASE_OR_COMMIT</version>
</dependency>
```

### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.WalshyDev:StreamDeck4J:$RELEASE_OR_COMMIT'
}
```