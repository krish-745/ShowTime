
document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const movieId = urlParams.get("id");    
    let allShows = [];
    //const now = new Date();
    const now = new Date();
    const date = {
        year: now.getFullYear(),
        month: now.getMonth() + 1, // JavaScript months are 0-indexed
        day: now.getDate()
    };
    console.log(date);


displayRelevantdates(date);

    if (movieId) {
        loadshows(movieId);
    } else {
        document.querySelector('.shows').innerHTML = 
            '<p style="color:red;">No movie selected. Please go back.</p>';
    }

    

    // Date item click handlers
    document.querySelectorAll('.date-item').forEach(item => {
        item.addEventListener('click', function() {
            document.querySelectorAll('.date-item').forEach(i => i.classList.remove('current'));
            this.classList.add('current');
            filterShowsByDate(this.dataset.date);
        });
    });
});

function filterShowsByDate(selectedDate) {
    console.log("Filtering shows for date:", selectedDate); // Debug log
    const showsContainer = document.querySelector('.shows');
    showsContainer.innerHTML = '';
    
    let html = '';
    const theaters = {};
    
    allShows.forEach(show => {
        // Parse the show date in DD/MM/YYYY format
        const showDate = formatDate(new Date(show.showTime));
        console.log("Show date:", showDate, "Selected date:", selectedDate); // Debug log
        
        if (showDate === selectedDate) {
            const theaterKey = show.theater.theaterId;
            if (!theaters[theaterKey]) {
                theaters[theaterKey] = {
                    name: show.theater.name,
                    location: show.theater.location,
                    showTimes: []
                };
            }
            
            const showTime = new Date(show.showTime);
            const formattedTime = showTime.toLocaleTimeString([], {
                hour: '2-digit',
                minute: '2-digit',
                hour12: true
            });
            
            // Calculate available seats
            const availableSeats = show.seats.filter(seat => !seat.booked).length;
            const totalSeats = show.seats.length;
            const seatsInfo = `${availableSeats}/${totalSeats} seats`;
            
            theaters[theaterKey].showTimes.push({
                time: formattedTime,
                showId: show.id,
                seatsInfo: seatsInfo
            });
        }
    });
    
    console.log("Theaters with shows:", theaters); // Debug log
    
    // Generate HTML for filtered shows
    for (const theaterId in theaters) {
        const theater = theaters[theaterId];
        html += `
            <div class="theatre">
                <div class="theatreinfo">
                    <p><strong>${theater.name}</strong></p>
                    <p>${theater.location}</p>
                </div>
                <div class="timings">
                    ${theater.showTimes.map(show => `
                        <button class="showtimings" data-show-id="${show.showId}">
                            <span class="time">${show.time}</span>
                            <span class="seats">${show.seatsInfo}</span>
                        </button>
                    `).join('')}
                </div>
            </div>
        `;
    }
    
    showsContainer.innerHTML = html || '<p>No shows available for this date</p>';
    
    // Reattach event listeners
    document.querySelectorAll('.showtimings').forEach(button => {
        button.addEventListener('click', function() {
            window.location.href = `booktickets.html?showId=${this.dataset.showId}`;
        });
    });
}

// function to format dates as DD/MM/YYYY
function formatDate(date) {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

function loadshows(movieId) {
    // First fetch movie details
    fetch(`http://localhost:8080/movies/${movieId}`)
        .then(response => {
            if (!response.ok) throw new Error('Movie not found');
            return response.json();
        })
        .then(movie => {
            document.querySelector('.Movie-name').innerHTML = `
                <p>${movie.name}</p>
                <p>${movie.genre} • ${Math.floor(movie.duration/60)}h ${movie.duration%60}m • ${movie.rating}/5</p>
            `;
        })
        .catch(error => {
            console.error("Error loading movie:", error);
            document.querySelector('.shows').innerHTML = 
                '<p style="color:red;">Error loading movie details</p>';
        });
    
    // fetch showtimes
    fetch(`http://localhost:8080/shows/${movieId}`)
        .then(response => {
            if (!response.ok) throw new Error('Showtimes not available');
            return response.json();
        })
        .then(shows => {
            allShows = shows;
            console.log(allShows);
            console.log("All shows loaded:", shows); // Debug log
            
            // Display today's shows by default
            const today = formatDate(new Date());
            console.log("Today's date:", today); // Debug log
            
            // Find and select today's date in the calendar
            let dateFound = false;
            document.querySelectorAll('.date-item').forEach(item => {
                if (item.dataset.date === today) {
                    item.classList.add('current');
                    filterShowsByDate(today);
                    dateFound = true;
                }
            });
            
            if (!dateFound && shows.length > 0) {
                // Fallback to first available date if today has no shows
                const firstShowDate = formatDate(new Date(shows[0].showTime));
                console.log("First show date:", firstShowDate); // Debug log
                
                document.querySelectorAll('.date-item').forEach(item => {
                    if (item.dataset.date === firstShowDate) {
                        item.classList.add('current');
                        filterShowsByDate(firstShowDate);
                        dateFound = true;
                    }
                });
                
                if (!dateFound) {
                    // If no matching date found in calendar, show all shows
                    displayAllShows(shows);
                }
            }
        })
        .catch(error => {
            console.error("Error loading shows:", error);
            document.querySelector('.shows').innerHTML = 
                '<p style="color:red;">Error loading showtimes</p>';
        });
}

// Fallback function to display all shows if date filtering fails
function displayAllShows(shows) {
    console.log("Displaying all shows"); // Debug log
    const showsContainer = document.querySelector('.shows');
    let html = '';
    const theaters = {};
    
    shows.forEach(show => {
        const theaterKey = show.theater.theaterId;
        if (!theaters[theaterKey]) {
            theaters[theaterKey] = {
                name: show.theater.name,
                location: show.theater.location,
                showTimes: []
            };
        }
        
        const showTime = new Date(show.showTime);
        const formattedTime = showTime.toLocaleTimeString([], {
            hour: '2-digit',
            minute: '2-digit',
            hour12: true
        });
        
        const availableSeats = show.seats.filter(seat => !seat.booked).length;
        const totalSeats = show.seats.length;
        const seatsInfo = `${availableSeats}/${totalSeats} seats`;
        
        theaters[theaterKey].showTimes.push({
            time: formattedTime,
            showId: show.id,
            seatsInfo: seatsInfo
        });
    });
    
    for (const theaterId in theaters) {
        const theater = theaters[theaterId];
        html += `
            <div class="theatre">
                <div class="theatreinfo">
                    <p><strong>${theater.name}</strong></p>
                    <p>${theater.location}</p>
                </div>
                <div class="timings">
                    ${theater.showTimes.map(show => `
                        <button class="showtimings" data-show-id="${show.showId}">
                            <span class="time">${show.time}</span>
                            <span class="seats">${show.seatsInfo}</span>
                        </button>
                    `).join('')}
                </div>
            </div>
        `;
    }
    
    showsContainer.innerHTML = html || '<p>No shows available</p>';
    
    document.querySelectorAll('.showtimings').forEach(button => {
        button.addEventListener('click', function() {
            window.location.href = `booktickets.html?showId=${this.dataset.showId}`;
        });
    });
}

// function displayRelevantdates(date){
//     let datehtml = document.querySelector('.date-scroller');
//     let html = '';
//     for(let i = 0;i < 4;i++){
//         html += `
//             <div class="date-item" data-date="${String(date.day+i).padStart(2,'0')}/${date.month}/2025">
//                         <div class="day">SUN</div>
//                         <div class="date">${date.day+i}</div>
//                         <div class="month">APRIL</div>
//                     </div>
//         `
//     }
//     html += `<div class="nav-arrow">
//                 <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
//                     <path d="M9 18l6-6-6-6"/>
//                 </svg>
//             </div>`
//     datehtml.innerHTML = html;
// }

function displayRelevantdates(dateObj) {
    let datehtml = document.querySelector('.date-scroller');
    let html = '';
    
    // Day names and month names arrays
    const dayNames = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
    const monthNames = ['JANUARY', 'FEBRUARY', 'MARCH', 'APRIL', 'MAY', 'JUNE', 
                      'JULY', 'AUGUST', 'SEPTEMBER', 'OCTOBER', 'NOVEMBER', 'DECEMBER'];

    for(let i = 0; i < 5; i++) {
        // Create a new date object for each day
        const currentDate = new Date(dateObj.year, dateObj.month - 1, dateObj.day + i);
        
        // Get date components
        const dayOfMonth = currentDate.getDate();
        const month = currentDate.getMonth() + 1; // Months are 0-indexed
        const year = currentDate.getFullYear();
        const dayOfWeek = dayNames[currentDate.getDay()];
        const monthName = monthNames[currentDate.getMonth()];

        html += `
            <div class="date-item" data-date="${String(dayOfMonth).padStart(2,'0')}/${String(month).padStart(2,'0')}/${year}">
                <div class="day">${dayOfWeek}</div>
                <div class="date">${dayOfMonth}</div>
                <div class="month">${monthName}</div>
            </div>
        `;
    }
    
    // html += `<div class="nav-arrow">
    //             <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
    //                 <path d="M9 18l6-6-6-6"/>
    //             </svg>
    //         </div>`;
    
    datehtml.innerHTML = html;
}

// Initialize with current date


function correctDate(date,i){
    if(date.getDate()+i > 31 && date.getMonth()+1<=7 && date.getMonth()%2 == 1){
        return 1;
    }
    else if(date.getDate()+i > 31 && date.getMonth()+1>7 && date.getMonth()%2 == 0){
        return 1;
    }
    else if(date.getDate()+i>30 && date.getMonth()+1>7 && (date.getMonth()+1)%2 == 1){
        return 1;
    }
    else if(date.getDate()+i>28 && date.getMonth()+1<=7 && (date.getMonth()+1)%2 == 0){
        if((date.getMonth() + 1) == 2){//for feb
            if(date.getFullYear()%4 == 0){
                if((date.getDate()+i)>29){
                    return 1;
                }
            }
            else{
                return 1;
            }
        }
        else if(date.getDate()+i>30){
            return 1;
        }
    }
}
function correctMonth(date,i){
    if(date.getDate()+i > 31 && date.getMonth()+1<=7 && date.getMonth()%2 == 1){
        return date.month()+2;
    }
    else if(date.getDate()+i > 31 && date.getMonth()+1>7 && date.getMonth()%2 == 0){
        return date.month()+2;
    }
    else if(date.getDate()+i>30 && date.getMonth()+1>7 && (date.getMonth()+1)%2 == 1){
        return date.month()+2;
    }
    else if(date.getDate()+i>28 && date.getMonth()+1<=7 && (date.getMonth()+1)%2 == 0){
        if((date.getMonth() + 1) == 2){//for feb
            if((date.getFullYear()%4 == 0 && date.getFullYear()%100 != 0) || (date.getFullYear()%400 == 0)){
                if((date.getDate()+i)>29){
                    return date.month()+2;
                }
            }
            else{
                return date.month()+2;
            }
        }
        else if(date.getDate()+i>30){
            return date.month()+2;
        }
    }
    return date.getMonth()+1;
}
