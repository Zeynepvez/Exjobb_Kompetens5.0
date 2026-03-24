function showRegister(){
    document.querySelector(".auth-container").classList.add("hidden")
    document.getElementById("registerBox").classList.remove("hidden")
}

function showLogin(){
    document.querySelector(".auth-container").classList.remove("hidden")
    document.getElementById("registerBox").classList.add("hidden")
}

/* REGISTER */

document.getElementById("registerForm").addEventListener("submit", function(e){

    e.preventDefault()

    let user = document.getElementById("regUser").value
    let pass = document.getElementById("regPass").value

    localStorage.setItem("username", user)
    localStorage.setItem("password", pass)

    alert("Account created! Please login.")

    showLogin()

})


/* LOGIN */
document.getElementById("loginForm").addEventListener("submit", function(e){

    e.preventDefault()

    let user = document.getElementById("loginUser").value
    let pass = document.getElementById("loginPass").value

    let savedUser = localStorage.getItem("username")
    let savedPass = localStorage.getItem("password")

    /* TEMP ACCOUNT */

    if(
        (user === "hej" && pass === "123") ||
        (user === savedUser && pass === savedPass)
    ){

        localStorage.setItem("loggedIn", true)

        window.location.href = "Home.html"

    }else{

        alert("Wrong login")

    }

})