report:
	./gradlew jacocoTestReport

build:
	./gradlew build

test:
	./gradlew test

lint:
	./gradlew checkstyleMain

clean:
	./gradlew clean

setup:
	./gradlew wrapper --gradle-version 8.10

sonar:
	./gradlew sonar --info
