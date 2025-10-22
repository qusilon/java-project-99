report:
	./gradlew jacocoTestReport

build:
	./gradlew clean build

test:
	./gradlew test

lint:
	./gradlew checkstyleMain

clean:
	./gradlew clean

setup:
	./gradlew wrapper --gradle-version 8.10
