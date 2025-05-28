document.addEventListener("DOMContentLoaded",function(){
    const urlParams = new URLSearchParams(window.location.search);
    const showId = urlParams.get("showId");
    console.log(showId);
    let ticketsbutton = document.querySelector('.ticketbutton');
    let numberoftickets = document.querySelector('.numtick');
    ticketsbutton.addEventListener("click",function(){
        showSuggestedSeats(showId ,numberoftickets.value);
    });
});

function showSuggestedSeats(showId, numberOfTickets) {
    fetch(`http://localhost:8080/seats/available/${showId}/${numberOfTickets}`)
        .then(response => response.json())
        .then(suggestedSeats => {
            // Encode only once
            const encodedSeats = encodeURIComponent(JSON.stringify(suggestedSeats));
            let totaltickets = document.querySelector('.numtick').value;
            console.log(totaltickets);
            // Use single ? and & for parameters
            if(totaltickets <= 10 && totaltickets > 0){
                window.location.href = 
                `ticketsplanner.html?showId=${showId}&suggested=${encodedSeats}&tickets=${totaltickets}`;
            }
            else if(totaltickets > 10){
                alert('Cannot book more than 10 tickets!!');
            }
            else{
                alert('Minimum 1 ticket required!!');
            }
        })
        .catch(console.error);
}