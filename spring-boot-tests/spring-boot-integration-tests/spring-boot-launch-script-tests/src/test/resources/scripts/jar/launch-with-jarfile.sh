source ./test-functions.sh
cp spring-boot-launch-script-tests.jar app-another.jar
export JARFILE=app-another.jar
launch_jar
await_app
curl -s http://127.0.0.1:8080/
