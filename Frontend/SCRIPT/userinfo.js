import {getUser} from './user.js';
let User = [];
let name;
let phoneNo;
let Email;

document.addEventListener("DOMContentLoaded", function() {
    // function to fetch booking data
    User = getUser();
    console.log(User);
    fetch(`http://localhost:8080/users/${User.id}`)
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        // check if data exists and has required properties
        if (!data) {
            throw new Error('No data received from server');
        }
        
        name = data.name || 'Not provided';
        phoneNo = data.phoneNumber || 'Not provided';
        Email = data.email || 'Not provided';
        
        console.log('User details:', { name, phoneNo, Email });

        const userInfo = document.createElement('div');
            userInfo.className = 'user-info';
            userInfo.innerHTML = `
                <h2>User Information</h2>
                <div class="username">Name: ${name}</div>
                <div class="mobileno">Phone: ${phoneNo}</div>
                <div class="email">Email: ${Email}</div>
            `;
            document.querySelector('.booking-container').appendChild(userInfo);
    })
    .catch(error => {
        console.error("Error fetching user data:", error);
    });
    
    async function fetchBookingData() {
        try {
            const response = await fetch(`http://localhost:8080/bookings/${User.id}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const bookings = await response.json();
            displayAllBookings(bookings);
        } catch (error) {
            console.error('Error fetching booking data:', error);
            document.querySelector('.username').textContent = `Name: ${name}`;
        }
    }
    
    

    // Function to display all bookings for a user
    function displayAllBookings(bookings) {
        if (bookings.length === 0) {
            document.querySelector('.booking-container').innerHTML = '<p>No bookings found</p>';
            return;
        }

        // Group bookings by user (assuming we might have multiple users)
        const bookingsByUser = {};
        bookings.forEach(booking => {
            const userId = booking.user.id;
            if (!bookingsByUser[userId]) {
                bookingsByUser[userId] = [];
            }
            bookingsByUser[userId].push(booking);
        });

        // Display each user's bookings
        for (const userId in bookingsByUser) {
            const userBookings = bookingsByUser[userId];
            const firstBooking = userBookings[0];
            
            // Create user info section
            // const userInfo = document.createElement('div');
            // userInfo.className = 'user-info';
            // userInfo.innerHTML = `'
            //     <h2>User Information</h2>
            //     <div class="username">Name: ${name}</div>
            //     <div class="mobileno">Phone: ${phoneNo}</div>
            //     <div class="email">Email: ${Email}</div>
            // `;
            // document.querySelector('.booking-container').appendChild(userInfo);

            // Create bookings section
            const bookingsSection = document.createElement('div');
            bookingsSection.className = 'bookings-section';
            bookingsSection.innerHTML = '<h2>Your Bookings</h2>';
            
            // Add each booking
            userBookings.forEach((booking, index) => {
                const bookingElement = createBookingElement(booking, index + 1);
                bookingsSection.appendChild(bookingElement);
            });
            
            document.querySelector('.booking-container').appendChild(bookingsSection);
        }
    }

    // Function to create a booking element
    function createBookingElement(booking, index) {
        const bookingElement = document.createElement('div');
        bookingElement.className = 'booking-card';
        
        // Format show time
        const showDateTime = new Date(booking.show.showTime);
        const formattedTime = showDateTime.toLocaleString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        // Get unique seat categories
        const seatCategories = [...new Set(booking.seats.map(seat => {
            return seat.seatCategory.replace('SEAT_CATEGORY_', '');
        }))];

        // Get seat numbers for display
        const seatNumbers = booking.seats.map(seat => 
            `Row ${seat.rowNumber}, Seat ${seat.seatNumberInRow}`
        ).join(' | ');

        bookingElement.innerHTML = `
            <div class="booking-header">
                <h3>Booking #${index}</h3>
                <div class="booking-status">Status: Confirmed</div>
            </div>
            <div class="booking-details">
                <div class="moviename"><strong>Movie:</strong> ${booking.show.movie.name}</div>
                <div class="theaterinfo"><strong>Theater:</strong> ${booking.show.theater.name}, ${booking.show.theater.location}</div>
                <div class="showtime"><strong>Show Time:</strong> ${formattedTime}</div>
                <div class="seatinfo"><strong>Seats:</strong> ${seatNumbers}</div>
                <div class="tickettype"><strong>Ticket Type(s):</strong> ${seatCategories.join(', ')}</div>
                <div class="totaltickets"><strong>Total Tickets:</strong> ${booking.seats.length}</div>
                <div class="totalprice"><strong>Total Price:</strong> ₹${booking.totalPrice}</div>
            </div>
        `;

        return bookingElement;
    }

    // Call the function to fetch and display data
    fetchBookingData();
});

