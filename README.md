# PaperlessREST

## HOWTO

### Steps to generate REST Server:

1. Start just with openapi-gen.sh and openapi.yaml
2. Run ```./openapi-gen.sh```
3. The generated solution will be in ```rest/``` - open with IntelliJ and try out if source was generated as expected.
4. move all files from the ```out/``` directory to your rest-project directory, e.g.
      ```cp -rf out/* rest```
4. Navigate to rest/pom.xml --> right-click --> "Add as Maven Project"
5. Run the server with ```mvn spring-boot:run```
6. Open the browser in http://localhost:8081/

### Next steps & hints:

* create fake implementations in ApiApiController to try out the REST-Server

* add ```logging.level.root=DEBUG``` to see a more verbose output