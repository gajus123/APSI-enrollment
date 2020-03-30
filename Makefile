setup-fe:
	@cd apsi-enrollment-fe && npm install

test-fe: setup-fe
	@cd apsi-enrollment-fe && ng test -- --watch=false

build-fe: setup-fe
	@cd apsi-enrollment-fe && ng build

setup-be:
	@cd apsi-enrollment-be && gradle wrapper && chmod +x gradlew

test-be: setup-be
	@cd apsi-enrollment-be && ./gradlew test

build-be: setup-be
	@cd apsi-enrollment-be && ./gradlew build
