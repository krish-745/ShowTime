document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const movieId = urlParams.get("id");
    console.log("Movie ID from URL:", movieId);
    if (movieId) {
        fetchMovieDetails(movieId);
    } else {
        console.error("Movie ID not found in URL.");
    }
    
});

function fetchMovieDetails(movieId) {
    fetch(`http://localhost:8080/movies/${movieId}`) // Fetch movie details from backend
        .then(response => {
            if (!response.ok) {
                throw new Error("Movie not found");
            }
            return response.json();
        })
        .then(movie => {
            displayMovieDetails(movie);
        })
        .catch(error => {
            console.error("Error fetching movie details:", error);
            document.querySelector('.movie-card').innerHTML = `<p style="color:red;">Movie not found</p>`;
        });
}

function displayMovieDetails(movie) {
    let html = `
        <div class="movie-image">
            <img src="${movie.posterPath}" alt="${movie.name}">
        </div>

        <!-- Movie Details -->
        <div class="movie-details">
            <h2>${movie.name}</h2>
            <p>${movie.genre}</p>
            <div class="text"><p class="movie-description">
                ${movie.overview}
            </p></div>
            <div class="book-button"><button class="booknow">Book Now</button></div>
        </div>
    `;

    document.querySelector('.movie-card').innerHTML = html;

    let booknow = document.querySelector('.booknow');
    booknow.addEventListener("click",function(){
        console.log("Book now button clicked");
        window.location.href = `theatreoptions.html?id=${movie.movieId}`;
    });
}
