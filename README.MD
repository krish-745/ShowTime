| Component      	| URL                                         	|
|----------------	|---------------------------------------------	|
| Swagger UI URL 	| http://localhost:8080/swagger-ui/index.html 	|
| H2 DB Console  	| http://localhost:8080/h2-console/login.jsp  	|
| Open API Spec  	| http://localhost:8080/api-docs              	|

**Change Notes**

23 March 2025

Commit #1

1. Added Priority queue based dynamic pricing implementation for ticket booking.
2. Fixed an issue where adjacent seat were not correctly calculated.
3. Added LinkedLinked based booking history implementation
4. Added HashMap based implementation for fast user search
5. Added DTOs for data transfer.

Commit #2

1. Updated README.MD

18 March 2025

Commit #1
1. Fixed an issue where seats were not getting booked.
2. Fixed an issue were adjacent seats were not generated
3. Added Open API spec dependency
4. Added Swagger configuration
5. Updated readme.md file

Commit #2
1. Added TMDB API integration to fetch actual movies using rest API
2. Implemented Trie data structure to perform fast search on movies and theatres
3. Added new endpoint "/movies/search/{searchText}" to generate auto search suggestion of movies
4. Added new columns in Movie object to store movie poster image URL and movie overview
5. Populated movie poster url with actual image poster image url which can be used directly in UI.