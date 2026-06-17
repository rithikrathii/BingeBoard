# Distributed Movie Review System
The Distributed Movie Review System is a project based on a microservice architecture. 
It was developed as part of the course "Programming Distributed Systems". 
The goal is to create a robust and scalable platform for browsing, rating and reviewing movies.

## System Architecture

### 1. Android App
* Language: Kotlin
* Platform: Android Virtual Device (AVD)
* Features:
   * User-friendly interface built with Jetpack Compose & Material 3
   * Browse movies with infinite scroll pagination
   * Search movies across full dataset
   * Advanced filter by genre, year range, age rating and language
   * Detail view with movie poster images, ratings and metadata
   * Write, read and delete reviews with star ratings (1-5)
   * "About" page with project and team information
   * User authentication
   * Connected to all 3 backend microservices (Auth, Movie Catalogue, Ratings & Reviews)
   * unit tests covering signup, login, search, filter, pagination and review validation

#### How the Android App Features Work

**Movie Loading**
On startup, the app loads all available movies (~21,000) from the Movie Catalogue Service by paginating through the `/movies/` endpoint (50 per request). These are displayed using infinite scroll — 20 movies render initially, and more load automatically as the user scrolls down.

**Genre Filter**
Genre chips appear at the top of the home screen, populated from the `/movies/genres` endpoint. Tapping a genre calls the `/movies/filter?genre=X` endpoint and loads all movies of that genre across the full dataset (not just the loaded ones) by paginating through all result pages. For example, tapping "Drama" loads all 12,000+ drama movies. Since movies can belong to multiple genres, a single movie may appear under several genre chips (e.g. a film tagged Thriller, Horror and Drama shows up in all three).

**Advanced Filter**
A filter icon in the top bar opens a bottom sheet where the user can filter by year range (min/max), age rating, and language. These combine with the selected genre and call `/movies/filter` with all active parameters. A Clear button resets all filters.

Note on age ratings: The dataset uses American (MPAA) and US TV rating systems, not European ones. Valid rating values include `G`, `PG`, `PG-13`, `R`, `NC-17`, the older `APPROVED` and `PASSED`, and TV ratings like `TV-G`, `TV-PG`, `TV-14`, `TV-MA`. European-style ratings such as PG-11, PG-15, or PG-16 do not exist in this dataset.

**Search**
The search bar performs a full-text search via the `/movies/search` endpoint with a 500ms debounce (it waits until the user stops typing before sending the request). The backend uses MongoDB `$text` search, which matches whole words and is case-insensitive. Results display through the same infinite scroll mechanism.

**Reviews**
On a movie's detail screen, logged-in users can write reviews with a 1-5 star rating, which are sent to the Ratings & Reviews Service (port 8002) with a JWT token. Users can read all reviews for a movie and delete their own reviews — the delete button only appears on reviews they authored.

**Robust Data Handling**
The `sample_mflix` dataset contains some movies with corrupted fields (e.g. malformed year values like `"1994è1998"`). The app parses these safely — corrupted entries are skipped individually rather than crashing the whole list, so every genre and filter still works reliably.

### 2. Authentication Microservice
* Technology: Python, FastAPI, PostgreSQL, Docker
* Port: `8000`
* Features:
   * User registration
   * User login
   * Password hashing with bcrypt
   * JWT-based authentication
   * Protected `/auth/me` endpoint
   * Role-based `/auth/admin-check` endpoint

### 3. Movie Catalogue Microservice
* Technology: Python, FastAPI, MongoDB Atlas, Docker
* Port: `8001`
* Dataset: MongoDB `sample_mflix` (movies collection)
* Features:
   * Paginated movie listing (`GET /movies/`)
   * Full-text search across title, plot and cast (`GET /movies/search`)
   * Filter by genre, year range, rating and language (`GET /movies/filter`)
   * List of all unique genres for UI dropdowns (`GET /movies/genres`)
   * Single movie details by ID (`GET /movies/{id}`)
   * Health check endpoint (`GET /health`)
   * Structured JSON logging
   * Non-root Docker container
   * 11 passing pytest tests covering all endpoints, pagination, search, filtering and error handling

#### Movie Catalogue Endpoints

| Method | Endpoint | Description | Query/Path Parameters |
|--------|----------|--------------|------------------------|
| GET | `/movies/` | Paginated list of movies | `page` (default 1), `limit` (default 10, max 50) |
| GET | `/movies/search` | Full-text search across title, plot, cast and genres, sorted by relevance | `q` (required), `page`, `limit` |
| GET | `/movies/filter` | Filter movies by genre, year range, rating and language (any combination) | `genre`, `year_min`, `year_max`, `rated`, `language`, `page`, `limit` |
| GET | `/movies/genres` | Alphabetically sorted list of all unique genres | none |
| GET | `/movies/{movie_id}` | Full details of a single movie | `movie_id` (MongoDB ObjectId as string) |
| GET | `/health` | Service health check | none |

Interactive API documentation is available at `http://localhost:8001/docs` while the service is running.

### 4. Ratings & Reviews Microservice
- Coming soon

## Running the Application

### i. Prerequisites
Before you begin, ensure the following tools are installed on your system:
- [Docker](https://www.docker.com/)
- [JDK 17 or higher](https://www.oracle.com/java/technologies/downloads/) (required for building the Android app)
- [Android Studio](https://developer.android.com/studio) with an Android Virtual Device (API 26+)
- [Visual Studio Code](https://code.visualstudio.com/) (optional, for viewing backend services)

### ii. Cloning the Project
- Open your terminal and navigate to your desired project directory:
cd ~/desktop  # or any directory of your choice

- Clone the project:
git clone https://gitlab.hsrw.eu/cloud-resilience-lab/lectures-mgk/pds-st26/group_1_05.git

- Open the project folder in Visual Studio Code:
code group_1_05

### iii. Setting Up Environment Variables
A `.env` file is already included in the repository with all required variables pre-configured. No additional setup is needed.

### iv. Running the Application with Docker
Make sure Docker Desktop is running in the background, then from the terminal run:
docker compose build

docker compose up
Once running, the following services will be available:
- Auth Service: `http://localhost:8000`
- Movie Catalogue Service: `http://localhost:8001`
- Ratings & Reviews Service: `http://localhost:8002`

### v. Running the Android Application
1. Open Android Studio
2. Open the project root folder `group_1_05`
3. Wait for Gradle sync to complete
4. Start an Android Virtual Device (API 26+)
5. Click **Run** to build and launch BingeBoard
6. Make sure Docker services are running before launching the app — the app connects to:
   - Auth Service: `http://10.0.2.2:8000`
   - Movie Catalogue: `http://10.0.2.2:8001`
   - Ratings & Reviews: `http://10.0.2.2:8002`

### vi. Common Issues & Troubleshooting
| Issue | Solution |
|-------|----------|
| Gradle sync fails in Android Studio | Go to File → Invalidate Caches → Restart Android Studio |
| App shows connection error | Make sure Docker services are running via `docker compose up` |
| Build errors in Android Studio | Go to Build → Clean Project → then Rebuild Project |
| Android Studio can't find SDK | Go to File → Project Structure → SDK Location and set correct path |
| App crashes on launch | Ensure AVD is running API 26 or higher |
| Movies not loading | Make sure Movie Catalogue Service is running on port 8001 |
| Login/Signup not working | Make sure Auth Service is running on port 8000 |
| Docker won't start | Make sure Docker Desktop is running in the background |
| Reviews not loading | Make sure Ratings & Reviews Service is running on port 8002 |
| Search not returning results | Use complete words rather than partial ones (e.g. "Godfather" instead of "Godfa"), as search matches whole words |
| App shows black screen on launch | Wait 10-15 seconds for all Docker services to fully start, then relaunch |
| Review submission fails | Make sure you are logged in before submitting a review |

## Contribution Matrix
| Name | Role | Contributions |
|------|------|---------------|
| Çağla Nur Yurdasal - 34367 | Project Lead | Coordinated team communication and task delegation, managed GitLab repository setup (docker-compose.yml, .gitignore, .env.example), resolved merge conflicts and onboarding issues, communicated with instructors regarding team changes (teammate withdrawal), in addition to full ownership of the Movie Catalogue Service (see below) |
| Rithik Kumar - 31522 | Android Frontend | UI implementation, Jetpack Compose screens, API integration, Navigation, Authentication flow, Infinite scroll pagination, Advanced filter UI (year, rating, language), Reviews integration (write/read/delete with star ratings), case-insensitive search, proper error messages for login/signup, 35 unit tests |
| Emre Banri - 33672 | Auth Service | FastAPI authentication service, PostgreSQL integration, password hashing, JWT login flow, protected endpoints, Docker Compose integration and testing |
|Çağla Nur Yurdasal - 34367| Movie Catalogue Service | FastAPI movie catalogue microservice (endpoints for listing, search, filtering, genres, single movie lookup), MongoDB Atlas integration with sample_mflix dataset, Pydantic models, structured JSON logging, Dockerfile with non-root user, docker-compose integration, 11 pytest tests, API field alignment support for Android integration | | Movie Service | |
| | Reviews Service | |

## Acknowledgements
The work of team members and their individual contributions are reflected in the GitLab commit history and the Contribution Matrix above.