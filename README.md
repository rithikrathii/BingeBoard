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
   * Review feature (read reviews per movie)
   * "About" page with project and team information
   * User authentication (Login & Signup)

### 2. Authentication Microservice
- Coming soon

### 3. Movie Catalogue Microservice
- Coming soon

### 4. Ratings & Reviews Microservice
- Coming soon

## Running the Application

### i. Prerequisites
Before you begin, ensure the following tools are installed on your system:
- [Docker](https://www.docker.com/)
- [Android Studio](https://developer.android.com/studio) with an Android Virtual Device (API 26+)
- [Visual Studio Code](https://code.visualstudio.com/) (optional, for viewing backend services)
- A valid MongoDB Atlas account with the `sample_mflix` dataset loaded

### ii. Cloning the Project
- Open your terminal and navigate to your desired project directory:
cd ~/desktop  # or any directory of your choice

- Clone the project:
git clone https://gitlab.hsrw.eu/cloud-resilience-lab/lectures-mgk/pds-st26/group_1_05.git

- Open the project folder in Visual Studio Code:
code group_1_05

### iii. Setting Up Environment Variables
- Create a file named `.env` in the root of the project, next to `docker-compose.yml`, and add the following variables:

MONGODB_URL=<MongoDB cluster URL from MongoDB Atlas>

- A sample `.env.example` file is provided in the repository as a reference.

> **Note:** The actual `.env` file is not committed to the repository for security reasons. Contact the team to obtain the MongoDB connection URL.

### iv. Running the Application with Docker
- Coming soon

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

## Contribution Matrix
| Name | Role | Contributions |
|------|------|---------------|
| | Project Lead | |
| Rithik Kumar - 31522 | Android Frontend | UI implementation, Jetpack Compose screens, API integration, Navigation, Authentication flow |
| | Auth Service | |
| | Movie Service | |
| | Reviews Service | |

## Acknowledgements
The work of team members on the components is documented in AUTHORS.md