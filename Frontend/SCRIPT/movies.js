
let moviesArray = [];
let searchTimeout;
let searchInput;

document.addEventListener("DOMContentLoaded", function() {
    fetchMovies();
    
    searchInput = document.querySelector('.search-container input');
    const searchButton = document.querySelector('.search-container button');

    // Create suggestions box
    const suggestionsBox = document.createElement('div');
    suggestionsBox.className = 'suggestions';
    document.querySelector('.search-container').appendChild(suggestionsBox);

    // Input handler
    searchInput.addEventListener("input", function() {
        clearTimeout(searchTimeout);
        const query = this.value.trim();
        
        if (query.length > 0) {
            searchTimeout = setTimeout(() => {
                fetch(`http://localhost:8080/movies/search/${encodeURIComponent(query)}`)
                    .then(response => response.json())
                    .then(showSuggestions)
                    .catch(console.error);
            }, 300);
        } else {
            clearSuggestions();
            displayMovies(moviesArray); // Show all movies when empty
        }
    });

    // Search button
    searchButton.addEventListener("click", function() {
        const query = searchInput.value.trim();
        if (query) {
            fetchMoviesStartingWith(query);
        }
        clearSuggestions();
    });

    // Enter key
    searchInput.addEventListener("keypress", function(e) {
        if (e.key === "Enter") {
            const query = searchInput.value.trim();
            if (query) {
                fetchMoviesStartingWith(query);
            } else {
                displayMovies(moviesArray); // Show all on empty Enter
            }
            clearSuggestions();
        }
    });
});

// Fetch all movies
function fetchMovies() {
    fetch("http://localhost:8080/movies")
        .then(response => response.json())
        .then(movies => {
            moviesArray = movies;
            displayMovies(movies);
        })
        .catch(console.error);
}

// Search function
function fetchMoviesStartingWith(query) {
    const matchedMovies = moviesArray.filter(movie => 
        movie.name.toLowerCase().startsWith(query.toLowerCase())
    );
    displayMovies(matchedMovies);
}

// Display movies
function displayMovies(movies) {
    const container = document.querySelector('.images');
    container.innerHTML = movies.length ? 
        movies.map(movie => {
            let ratingText = movie.rating == 0 ? 'Unrated' : movie.rating;
            return `
                <div class="room" onclick="openPage('${movie.movieId}')">
                    <img src="${movie.posterPath}" alt="${movie.name}">
                    <h3>${movie.name}</h3>
                    <div class="stylerating"><p>Rating: ${ratingText}</p></div>
                </div>
            `;
        }).join('') : 
        '<div class="Error">No movies found</div>';
}

// Suggestions functions
function showSuggestions(names) {
    const suggestionsBox = document.querySelector('.suggestions');
    suggestionsBox.innerHTML = names.map(name => `
        <div class="suggestion" data-name="${name}">${name}</div>
    `).join('');
    suggestionsBox.classList.add('show');

    suggestionsBox.querySelectorAll('.suggestion').forEach(item => {
        item.addEventListener('click', () => {
            const selectedName = item.getAttribute('data-name');
            searchInput.value = selectedName;
            console.log(selectedName);
            clearSuggestions();
            const matchedMovies = moviesArray.filter(movie => 
                movie.name.toLowerCase() === selectedName.toLowerCase()
            );
            displayMovies(matchedMovies);
        });
    });
}

function clearSuggestions() {
    const suggestionsBox = document.querySelector('.suggestions');
    suggestionsBox.classList.remove('show');
    suggestionsBox.innerHTML = '';
}

function openPage(movieId) {
    window.location.href = `moviebooking.html?id=${movieId}`;
}