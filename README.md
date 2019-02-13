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
    <version>1.1</version>
</dependency>
```

### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.WalshyDev:StreamDeck4J:1.1'
}
```

### Logging Framework - SLF4J
SD4J uses [SLF4J](https://www.slf4j.org/) to log its messages.

That means you should add some SLF4J implementation to your build path in addition to SD4J.
If no implementation is found, following message will be printed to the console on startup:
```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

The most popular implementations are [Log4j 2](https://logging.apache.org/log4j/2.x/) and [Logback](https://logback.qos.ch/). [Here's a nice example of a logback.xml](https://gist.github.com/WalshyDev/dfcd1f155b71c68bf596deb44bf6e15f)
<!-- TODO: Implement fallback logger -->