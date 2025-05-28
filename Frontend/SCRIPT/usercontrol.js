//using module so that we donot have to send the user data again and again through urlparams
import { user, updateUser } from './user.js';

document.addEventListener("DOMContentLoaded", function () {
    console.log("Script loaded successfully.");
    document.getElementById("userId").value = "";
    document.getElementById("password").value = "";
    
    let loginButton = document.getElementById("loginBtn");
    let signupLink = document.getElementById("signupLink");

    if (!loginButton) {
        console.error("Login button not found! Check your HTML.");
        console.log("issue in code!!");
        return;
    }

    if (signupLink) {
        signupLink.addEventListener("click", function (event) {
            event.preventDefault(); // Prevent default anchor behavior
            console.log("Signup link clicked. Redirecting...");
            window.location.href = "signup.html"; // Redirect to signup page
        });
    } else {
        console.error("Signup link not found in HTML.");
    }

    loginButton.addEventListener("click", function () {
        console.log("Login button clicked.");

        let userId = document.getElementById("userId").value;
        let password = document.getElementById("password").value;
        //if either of the input box is not filled then ask the user to enter both
        if (!userId || !password) {
            alert("Please enter both ID and Password.");
            return;
        }

        console.log(`fetching user ${userId} from backend...`);

        fetch(`http://localhost:8080/users/${userId}/${password}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Invalid credentials");
                }
                return response.json();
            })
            .then(data => {
                console.log("User authenticated:", data);
                alert(`Welcome, ${data.name || "User"}!`);
                //storing the info in module created in './user.js' 
                updateUser(data);
                //now we will load the home page after successful login ;)
                window.location.href = `front.html?userID = ${data.id}`;
            })
            .catch(error => {
                console.error("Error:", error);
                alert("Login failed : "+ error.message);
            });
    });
});
