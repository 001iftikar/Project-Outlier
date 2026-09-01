package com.iftikar.outlier.feature.home.impl

import com.iftikar.outlier.core.models.Post
import com.iftikar.outlier.core.models.User

data class HomeScreenState(
    val posts: List<Post> = benchmarkPosts,
    val imageIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface HomeScreenAction {
    data object OnRetry : HomeScreenAction
}

val tempPosts = listOf(

    Post(
        id = 1,
        title = "Building a Kotlin Weather App",
        desc = "A modern Android weather application built with Kotlin and Jetpack Compose, featuring location-based forecasts and a clean Material 3 interface.",
        images = listOf(
            "https://img.magnific.com/free-photo/lavender-field-sunset-near-valensole_268835-3910.jpg?semt=ais_hybrid&w=740&q=80",
            "https://img.magnific.com/free-photo/kirkjufell-sunrise-iceland-beautiful-landscape_335224-685.jpg?semt=ais_hybrid&w=740&q=80"
        ),
        github = "https://github.com/example/weather-app",
        liveLink = "https://example.com/weather",
        techStack = listOf(
            "Kotlin",
            "Jetpack Compose",
            "Room",
            "Retrofit"
        ),
        tags = listOf(
            "Android",
            "Kotlin",
            "Weather"
        ),
        user = User(
            id = "user1",
            name = "Ryu",
            email = "ryu@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 2,
        title = "Spring Boot Developer Platform",
        desc = "A backend service for a developer-focused social platform with JWT authentication, email verification, PostgreSQL persistence and REST APIs.",
        images = listOf(
            "https://media.greatbigphotographyworld.com/wp-content/uploads/2014/11/Landscape-Photography-steps.jpg"
        ),
        github = "https://github.com/example/developer-platform",
        liveLink = "https://example.com/developer-platform",
        techStack = listOf(
            "Java",
            "Spring Boot",
            "PostgreSQL",
            "JWT"
        ),
        tags = listOf(
            "Backend",
            "Spring Boot",
            "Java"
        ),
        user = User(
            id = "user2",
            name = "Alex",
            email = "alex@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 3,
        title = "Personal Finance Tracker",
        desc = "A personal finance application that helps users track expenses, categorize transactions and visualize monthly spending habits.",
        images = listOf(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_o6eFPXn_smQa2LVubruZUSWugCr_C4oXZtigQexCsEw4HREn_wOlrIw&s=10",
            "https://img.magnific.com/free-vector/hand-painted-sunset-mountain-trees-landscape_1048-19076.jpg?semt=ais_hybrid&w=740&q=80",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZAESFTIxD3L0C6tEc3G9V2UB4PMJPnKZuO7nKIVBngdjXg3NDFk2ne2A&s=10"
        ),
        github = "https://github.com/example/finance-tracker",
        liveLink = "https://example.com/finance",
        techStack = listOf(
            "Kotlin",
            "Jetpack Compose",
            "Room",
            "Charts"
        ),
        tags = listOf(
            "Finance",
            "Android",
            "Compose"
        ),
        user = User(
            id = "user3",
            name = "Mira",
            email = "mira@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 4,
        title = "Real-Time Chat Application",
        desc = "A real-time messaging application with online presence, private conversations and instant message delivery.",
        images = listOf(
            "https://img.magnific.com/free-photo/lavender-field-sunset-near-valensole_268835-3910.jpg?semt=ais_hybrid&w=740&q=80"
        ),
        github = "https://github.com/example/chat-app",
        liveLink = "https://example.com/chat",
        techStack = listOf(
            "Kotlin",
            "WebSockets",
            "Spring Boot",
            "PostgreSQL"
        ),
        tags = listOf(
            "Chat",
            "RealTime",
            "WebSockets"
        ),
        user = User(
            id = "user4",
            name = "Sam",
            email = "sam@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 5,
        title = "Open Source Habit Tracker",
        desc = "A lightweight habit tracker designed around simple daily goals, streaks and progress visualization.",
        images = listOf(
            "https://img.magnific.com/free-photo/kirkjufell-sunrise-iceland-beautiful-landscape_335224-685.jpg?semt=ais_hybrid&w=740&q=80",
            "https://media.greatbigphotographyworld.com/wp-content/uploads/2014/11/Landscape-Photography-steps.jpg"
        ),
        github = "https://github.com/example/habit-tracker",
        liveLink = "https://example.com/habits",
        techStack = listOf(
            "Kotlin",
            "Jetpack Compose",
            "DataStore"
        ),
        tags = listOf(
            "OpenSource",
            "Productivity",
            "Android"
        ),
        user = User(
            id = "user5",
            name = "Noah",
            email = "noah@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 6,
        title = "AI-Powered Notes",
        desc = "A notes application that uses AI to summarize long notes and extract important action items.",
        images = listOf(
            "https://img.magnific.com/free-vector/hand-painted-sunset-mountain-trees-landscape_1048-19076.jpg?semt=ais_hybrid&w=740&q=80"
        ),
        github = "https://github.com/example/ai-notes",
        liveLink = "https://example.com/ai-notes",
        techStack = listOf(
            "Kotlin",
            "Python",
            "FastAPI",
            "OpenAI"
        ),
        tags = listOf(
            "AI",
            "MachineLearning",
            "Notes"
        ),
        user = User(
            id = "user6",
            name = "Maya",
            email = "maya@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 7,
        title = "Movie Recommendation Engine",
        desc = "A recommendation system that suggests movies based on user preferences, watch history and similarity scores.",
        images = listOf(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_o6eFPXn_smQa2LVubruZUSWugCr_C4oXZtigQexCsEw4HREn_wOlrIw&s=10",
            "https://img.magnific.com/free-photo/lavender-field-sunset-near-valensole_268835-3910.jpg?semt=ais_hybrid&w=740&q=80"
        ),
        github = "https://github.com/example/movie-recommender",
        liveLink = "https://example.com/movies",
        techStack = listOf(
            "Python",
            "FastAPI",
            "PostgreSQL",
            "Machine Learning"
        ),
        tags = listOf(
            "ML",
            "Recommendations",
            "Python"
        ),
        user = User(
            id = "user7",
            name = "Dan",
            email = "dan@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 8,
        title = "Developer Portfolio Builder",
        desc = "A platform where developers can create portfolio pages, showcase projects and share their technical skills.",
        images = listOf(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZAESFTIxD3L0C6tEc3G9V2UB4PMJPnKZuO7nKIVBngdjXg3NDFk2ne2A&s=10"
        ),
        github = "https://github.com/example/portfolio-builder",
        liveLink = "https://example.com/portfolio",
        techStack = listOf(
            "React",
            "TypeScript",
            "Spring Boot",
            "PostgreSQL"
        ),
        tags = listOf(
            "Portfolio",
            "Web",
            "Developers"
        ),
        user = User(
            id = "user8",
            name = "Liam",
            email = "liam@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 9,
        title = "Offline First Android App",
        desc = "An Android application designed around an offline-first architecture with local caching and background synchronization.",
        images = listOf(
            "https://media.greatbigphotographyworld.com/wp-content/uploads/2014/11/Landscape-Photography-steps.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_o6eFPXn_smQa2LVubruZUSWugCr_C4oXZtigQexCsEw4HREn_wOlrIw&s=10",
            "https://img.magnific.com/free-vector/hand-painted-sunset-mountain-trees-landscape_1048-19076.jpg?semt=ais_hybrid&w=740&q=80"
        ),
        github = "https://github.com/example/offline-app",
        liveLink = "https://example.com/offline",
        techStack = listOf(
            "Kotlin",
            "Room",
            "WorkManager",
            "Jetpack Compose"
        ),
        tags = listOf(
            "OfflineFirst",
            "Android",
            "Architecture"
        ),
        user = User(
            id = "user9",
            name = "Oliver",
            email = "oliver@example.com",
            role = "DEVELOPER"
        )
    ),

    Post(
        id = 10,
        title = "Cloud Deployment Dashboard",
        desc = "A dashboard for monitoring deployed services, viewing application health and tracking deployment history.",
        images = listOf(
            "https://img.magnific.com/free-photo/kirkjufell-sunrise-iceland-beautiful-landscape_335224-685.jpg?semt=ais_hybrid&w=740&q=80"
        ),
        github = "https://github.com/example/cloud-dashboard",
        liveLink = "https://example.com/dashboard",
        techStack = listOf(
            "Java",
            "Spring Boot",
            "Docker",
            "PostgreSQL"
        ),
        tags = listOf(
            "Cloud",
            "DevOps",
            "Docker"
        ),
        user = User(
            id = "user10",
            name = "Ethan",
            email = "ethan@example.com",
            role = "DEVELOPER"
        )
    )
)

val benchmarkPosts = List(500) { index ->
    val basePost = tempPosts[index % tempPosts.size]

    basePost.copy(
        id = index + 1,
        title = "${basePost.title} #$index"
    )
}