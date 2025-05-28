document.addEventListener("DOMContentLoaded", function () {
    let signupButton = document.getElementById("signupBtn");

    signupButton.addEventListener("click", function () {
        let name = document.getElementById("name").value;
        let email = document.getElementById("email").value;
        let phone = parseInt(document.getElementById("phone").value, 10);
        let password = document.getElementById("password").value;
        let confirmPassword = document.getElementById("confirmPassword").value;
        console.log(phone);
        if (!name || !email || !phone || !password || !confirmPassword) {
            alert("All fields are required!");
            return;
        }

        if (password !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        let userData = {
            name: name,
            email: email,
            phoneNumber: phone,
            password: password
        };

        fetch("http://localhost:8080/users", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(userData)
        })
        .then(response => response.json())
        .then(data => {
            alert("Signup successful! You can now log in.");
            window.location.href = "usercontrol.html";
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Signup failed. Please try again.");
        });
    });
});
