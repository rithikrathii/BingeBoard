package com.example.bingeboard.data.repository

import com.example.bingeboard.data.model.Movie
import com.example.bingeboard.data.model.Review
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockMovieRepository @Inject constructor() : MovieRepository {

    private val movies = listOf(
        Movie(
            id = 1,
            title = "Interstellar",
            genre = listOf("Sci-Fi", "Thriller"),
            year = 2014,
            duration = "2h 49m",
            rating = 8.7,
            reviewCount = "1.6M",
            ageRating = "PG-13",
            description = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival. With Earth on the brink of catastrophe, Cooper must leave his family and venture beyond our galaxy to discover whether mankind has a future among the stars.",
            posterRes = 0, // Placeholder
            isTopRated = true
        ),
        Movie(
            id = 2,
            title = "The Dark Knight",
            genre = listOf("Action"),
            year = 2008,
            duration = "2h 32m",
            rating = 9.0,
            reviewCount = "2.8M",
            ageRating = "PG-13",
            description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
            posterRes = 1,
            isTopRated = true
        ),
        Movie(
            id = 3,
            title = "Oppenheimer",
            genre = listOf("Drama"),
            year = 2023,
            duration = "3h",
            rating = 8.9,
            reviewCount = "600K",
            ageRating = "R",
            description = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.",
            posterRes = 2,
            isTopRated = true
        ),
        Movie(
            id = 4,
            title = "Inception",
            genre = listOf("Sci-Fi"),
            year = 2010,
            duration = "2h 28m",
            rating = 8.8,
            reviewCount = "2.5M",
            ageRating = "PG-13",
            description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
            posterRes = 3,
            isTopRated = true
        ),
        Movie(
            id = 5,
            title = "Dune Part Two",
            genre = listOf("Sci-Fi", "Drama"),
            year = 2024,
            duration = "2h 46m",
            rating = 8.5,
            reviewCount = "400K",
            ageRating = "PG-13",
            description = "Paul Atreides unites with Chani and the Fremen while on a warpath of revenge against the conspirators who destroyed his family.",
            posterRes = 4,
            isTopRated = true
        ),
        Movie(
            id = 6,
            title = "The Godfather",
            genre = listOf("Drama"),
            year = 1972,
            duration = "2h 55m",
            rating = 9.2,
            reviewCount = "2M",
            ageRating = "R",
            description = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
            posterRes = 5,
            isTopRated = true
        )
    )

    private val reviews = listOf(
        Review(1, 1, "Marcus K.", "MK", "Dec 12, 2023", 5, "A cinematic masterpiece. Nolan pushes the boundaries of science fiction while delivering an emotional story."),
        Review(2, 1, "Sara F.", "SF", "Nov 28, 2023", 4, "Visually stunning and intellectually stimulating. The docking sequence is one of the greatest scenes ever."),
        Review(3, 2, "John D.", "JD", "Jan 05, 2024", 5, "The best superhero movie ever made. Heath Ledger's performance is legendary."),
        Review(4, 3, "Emily W.", "EW", "Aug 15, 2023", 5, "A haunting and powerful portrait of a man who changed the world. Cillian Murphy is incredible."),
        Review(5, 4, "Alex T.", "AT", "Oct 15, 2023", 5, "Hans Zimmer's score combined with the incredible visuals create an unforgettable experience.")
    )

    override suspend fun getAllMovies(): List<Movie> = movies

    override suspend fun getMovieById(id: Int): Movie? = movies.find { it.id == id }

    override suspend fun getReviewsForMovie(movieId: Int): List<Review> = reviews.filter { it.movieId == movieId }

    override suspend fun getGenres(): List<String> = listOf("All", "Action", "Drama", "Sci-Fi", "Comedy", "Thriller")

    override suspend fun searchMovies(query: String): List<Movie> = movies.filter { it.title.contains(query, ignoreCase = true) }

    override suspend fun getMoviesByGenre(genre: String): List<Movie> = if (genre == "All") movies else movies.filter { it.genre.contains(genre) }
}
