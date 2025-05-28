import { getUser } from './user.js';
//will use to retrieve info about the seats the user booked from userid
let currentUser = [];

let seatsPrice = [];
let totalPrice = 0;
let maxSeats = 3;
let selectedSeats = [];
document.addEventListener("DOMContentLoaded", async function () {
    currentUser = getUser();
    console.log("Current user:", currentUser);
    const urlParams = new URLSearchParams(window.location.search);
    let suggestedSeatsParam = urlParams.get('suggested');
    const showId = urlParams.get('showId');
    const tickets = urlParams.get('tickets');
    console.log(tickets);
    maxSeats = Number(tickets);
    await getPrice(showId);
    loadSeatsData(showId);
    //getPrice(showId);

    //getPrice(showId);
    if (typeof suggestedSeatsParam === "string") {
        try {
            suggestedSeatsParam = JSON.parse(suggestedSeatsParam);
        } catch (error) {
            console.error("Error parsing suggestedSeatsParam:", error);
        }
    }
        
    console.log("Show ID:", showId);
    console.log("Suggested Seats:", suggestedSeatsParam);

    let suggestedSeatIds = [];

suggestedSeatsParam.forEach(function(seatIds , index){
    suggestedSeatIds[index] = seatIds.id;
});

console.log("Final seat IDs:", suggestedSeatIds);
    // Initialize selected seats array
    selectedSeats = [];

    // Load seats data
    loadSeatsData(showId);

    // Function to load seats data from API
    function loadSeatsData(showId) {
        fetch(`http://localhost:8080/shows/seats/${showId}`)
            .then(response => response.json())
            .then(function (data) {
                renderSeatingMap(data);
            })
            .catch(error => {
                console.error("Error fetching seats data:", error);
                // if sample data loads that means api failed to fetch
                useSampleData();
            });
    }

    // Function to use sample data for testing
    function useSampleData() {
        // This is the data you provided
        const sampleData = {
            "bookedSeats": [
                { "id": 2, "rowNumber": 1, "seatNumberInRow": 1, "seatCategory": null, "booked": true },
                // ... other booked seats
            ],
            "unbookedSeats": [
                { "id": 82, "rowNumber": 1, "seatNumberInRow": 3, "seatCategory": null, "booked": false },
                // ... other unbooked seats
            ]
        };

        renderSeatingMap(sampleData);
    }

    // Function to render the seating map
    function renderSeatingMap(data) {
        const seatingMap = document.getElementById('seating-map');
        seatingMap.innerHTML = '';

        // combine booked and unbooked seats
        const allSeats = [...data.bookedSeats, ...data.unbookedSeats];

        // find max row and column
        const maxRow = Math.max(...allSeats.map(seat => seat.rowNumber));
        const maxSeatInRow = Math.max(...allSeats.map(seat => seat.seatNumberInRow));

        // Create a 2D array to store seat data
        const seatMap = {};

        // Fill the seat map
        allSeats.forEach(seat => {
            if (!seatMap[seat.rowNumber]) {
                seatMap[seat.rowNumber] = {};
            }
            seatMap[seat.rowNumber][seat.seatNumberInRow] = seat;
        });
        console.log(seatMap);
        // Render rows (from front to back)
        for (let row = maxRow; row >= 1; row--) {
            const rowElement = document.createElement('div');
            rowElement.className = 'row';

            // Add row number
            const rowNumber = document.createElement('div');
            rowNumber.className = 'row-number';
            rowNumber.textContent = row;
            rowElement.appendChild(rowNumber);

            // Add seats for this row
            for (let seatNum = 1; seatNum <= maxSeatInRow; seatNum++) {
                const seatElement = document.createElement('div');
                seatElement.className = 'seat';

                // if we have data for this seat
                if (seatMap[row] && seatMap[row][seatNum]) {
                    const seatData = seatMap[row][seatNum];
                    seatElement.textContent = seatNum;
                    seatElement.dataset.id = seatData.id;
                    seatElement.dataset.row = seatData.rowNumber;
                    seatElement.dataset.seatNumber = seatData.seatNumberInRow;

                    // Assign seat pricing based on row number
                    if (seatData.rowNumber === 1) {
                        seatElement.dataset.price = seatsPrice["SEAT_CATEGORY_LUXURY"];
                    } else if (seatData.rowNumber === 2 || seatData.rowNumber === 3) {
                        seatElement.dataset.price = seatsPrice["SEAT_CATEGORY_PREMIUM"];
                    } else if (seatData.rowNumber === 4 || seatData.rowNumber === 5) {
                        seatElement.dataset.price = seatsPrice["SEAT_CATEGORY_ECONOMY"];
                    }

                    // show price on hover
                    seatElement.title = `₹${seatElement.dataset.price}`;


                    // Check if seat is booked
                    if (seatData.booked) {
                        seatElement.classList.add('booked');
                    } else {
                        // Check if seat is in suggested seats
                        const isSuggested = suggestedSeatIds.includes(seatData.id);

                        if (isSuggested) {
                            seatElement.classList.add('suggested');
                            console.log("Marked seat as suggested:", seatData.id);
                        }

                        // Add click event to selectable seats
                        seatElement.addEventListener('click', function () {
                            toggleSeatSelection(this, seatData);
                        });
                    }
                } else {
                    // Empty seat or aisle
                    seatElement.style.visibility = 'hidden';
                }

                rowElement.appendChild(seatElement);
            }

            seatingMap.appendChild(rowElement);
        }
    }

    // Function to toggle seat selection
    // Function to toggle seat selection
// function toggleSeatSelection(seatElement, seatData) {
//     if (seatElement.classList.contains('booked')) {
//         return; // Can't select already booked seats
//     }
//     if (!seatData.price) {
//         seatData.price = seatElement.dataset.price;
//     }
//     if (seatElement.classList.contains('selected')) {
//         // Deselect the seat
//         seatElement.classList.remove('selected');

//         // If it was a suggested seat, restore the suggested class
//         if (suggestedSeatIds.includes(seatData.id)) {
//             seatElement.classList.add('suggested');
//         }

//         // Remove from selected seats array
//         selectedSeats = selectedSeats.filter(seat => seat.id !== seatData.id);
        
//     } else {
//         // Select the seat
//         seatElement.classList.add('selected');

//         // Remove suggested class if present
//         seatElement.classList.remove('suggested');

//         // Add to selected seats array
//         selectedSeats.push(seatData);
//     }

//     // Update booking summary, including total price
//     console.log(selectedSeats);
//     updateBookingSummary();
// }

function toggleSeatSelection(seatElement, seatData) {
    if (seatElement.classList.contains('booked')) return;
    console.log( seatData);
    if (!seatData.price) {
        seatData.price = seatElement.dataset.price;
    }

    const alreadySelectedIndex = selectedSeats.findIndex(seat => seat.id === seatData.id);

    if (alreadySelectedIndex !== -1) {
        // Deselect seat
        selectedSeats.splice(alreadySelectedIndex, 1);
        seatElement.classList.remove('selected');

        // Reapply suggested class if needed
        if (suggestedSeatIds.includes(seatData.id)) {
            seatElement.classList.add('suggested');
        }

    } else {
        // Enforce seat limit
        if (selectedSeats.length >= maxSeats) {
            const removedSeat = selectedSeats.shift(); // Deque
            const removedElement = document.querySelector(`.seat[data-id="${removedSeat.id}"]`);
            if (removedElement) {
                removedElement.classList.remove('selected');
                if (suggestedSeatIds.includes(removedSeat.id)) {
                    removedElement.classList.add('suggested');
                }
            }
        }

        // Select current seat
        selectedSeats.push(seatData);
        seatElement.classList.add('selected');
        seatElement.classList.remove('suggested');
    }

    updateBookingSummary();
}


    // Function to update booking summary
    function updateBookingSummary() {
        const selectedSeatsText = document.getElementById('selected-seats-text');
        const totalSeatsElement = document.getElementById('total-seats');
        const bookButton = document.getElementById('book-button');

        if (selectedSeats.length === 0) {
            selectedSeatsText.textContent = 'None';
            totalSeatsElement.textContent = '0';
            bookButton.disabled = true;
            document.getElementById('total-price').textContent = 'Total Price: ₹0';
        } else {
            // const seatsInfo = selectedSeats.map(seat => `Row ${seat.rowNumber}, Seat ${seat.seatNumberInRow}`).join('; ');

            //chatgpt
            // const seatsInfo = selectedSeats.map(seat => 
            //     `Row ${seat.rowNumber}, Seat ${seat.seatNumberInRow} (₹${seatElement.dataset.price})`
            // ).join('; ');
            
            // const totalPrice = selectedSeats.reduce((sum, seat) => sum + parseInt(seatElement.dataset.price), 0);

            const seatsInfo = selectedSeats.map(seat => 
                `Row ${seat.rowNumber}, Seat ${seat.seatNumberInRow} (₹${seat.price})`
            ).join('; ');
            
            totalPrice = selectedSeats.reduce((sum, seat) => sum + parseInt(seat.price), 0);
            document.getElementById('total-price').textContent = `Total Price: ₹${totalPrice}`;
            
            selectedSeatsText.textContent = seatsInfo;
            totalSeatsElement.textContent = selectedSeats.length;
            bookButton.disabled = false;
        }
    }

    // Book button event listener
    document.getElementById('book-button').addEventListener('click', function () {
        if (selectedSeats.length === 0) return;
    
        const seatIds = selectedSeats.map(seat => seat.id);
        console.log(seatIds);
    
        // preparing booking data with userId included imported from user.js 
        const bookingData = {
            userId: currentUser.id, 
            showId: showId,
            seatIds: seatIds
        };
    
        console.log("Booking data:", bookingData);
    
        // Send booking request to server
        fetch('http://localhost:8080/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bookingData)
        })
            .then(response => response.json())
            .then(data => {
                alert(`Booking successful!\nTotal Price paid = ₹${totalPrice}`);
    
                // Mark selected seats as booked in UI
                // selectedSeats.forEach(seat => {
                //     const seatElement = document.querySelector(`.seat[data-id="${seat.id}"]`);
                //     if (seatElement) {
                //         seatElement.classList.remove('selected');
                //         seatElement.classList.add('booked');
                //         seatElement.removeEventListener('click', toggleSeatSelection);
                //     }
                // });

                // // Clear selected seats
                // selectedSeats = [];
                // updateBookingSummary();
                window.location.href = 'front.html';
            })
            .catch(error => {
                console.error("Error booking seats:", error);
                alert('There was an error processing your booking. Please try again.');
            });
    });
});

async function getPrice(showId) {
    await fetch(`http://localhost:8080/shows/price/${showId}`)
        .then(response => response.json())
        .then(seatsprice=>{
            seatsPrice = seatsprice; // getting the dynamic ticket pricing from each show
            console.log(seatsPrice);
        })
        .catch(console.error);
        
}

