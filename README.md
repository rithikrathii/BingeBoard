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
   * Browse, sort and filter movies by genre and search
   * Detail view with movie poster images, ratings and metadata
   * Review feature (read and write reviews per movie)
   * "About" page with project and team information
   * User authentication (Login & Signup)

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

### v. Running the Android Application
1. Open Android Studio
2. Open the project root folder `group_1_05`
3. Wait for Gradle sync to complete
4. Start an Android Virtual Device (API 26+)
5. Click **Run** to build and launch BingeBoard
6. Make sure Docker services are running before launching the app — the app connects to:
   - Auth Service: `http://10.0.2.2:8000`
   - Movie Catalogue: `http://10.0.2.2:8001`

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

## Contribution Matrix
| Name | Role | Contributions |
|------|------|---------------|
| Çağla Nur Yurdasal - 34367 | Project Lead | Coordinated team communication and task delegation, managed GitLab repository setup (docker-compose.yml, .gitignore, .env.example), resolved merge conflicts and onboarding issues, communicated with instructors regarding team changes (teammate withdrawal), in addition to full ownership of the Movie Catalogue Service (see below) |
| Rithik Kumar - 31522 | Android Frontend | UI implementation, Jetpack Compose screens, API integration, Navigation, Authentication flow, Unit tests |
| Emre Banri - 33672 | Auth Service | FastAPI authentication service, PostgreSQL integration, password hashing, JWT login flow, protected endpoints, Docker Compose integration and testing |
|Çağla Nur Yurdasal - 34367| Movie Catalogue Service | FastAPI movie catalogue microservice (endpoints for listing, search, filtering, genres, single movie lookup), MongoDB Atlas integration with sample_mflix dataset, Pydantic models, structured JSON logging, Dockerfile with non-root user, docker-compose integration, 11 pytest tests, API field alignment support for Android integration | | Movie Service | |
| | Reviews Service | |

## Acknowledgements
The work of team members and their individual contributions are reflected in the GitLab commit history and the Contribution Matrix above.