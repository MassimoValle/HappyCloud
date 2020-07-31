(function(){
    document.getElementById("registrationButton").addEventListener("click", (event) => {
        event.preventDefault();

        var form = event.target.closest("form");

        var firstPassword = document.getElementById("id_registrationForm").elements["password"].value;
        var secondPassword = document.getElementById("id_registrationForm").elements["confPassword"].value;

        if (firstPassword.localeCompare(secondPassword) === 0 && form.checkValidity()) {
            makeCall("POST", "Registration", form,
                function (request) {
                    if (request.readyState === XMLHttpRequest.DONE) {
                        var message = request.responseText;
                        switch (request.status) {
                            case 200:
                                sessionStorage.setItem("username", message);
                                window.location.href = "home.html";
                                break;
                            case 400:   //bad request
                                document.getElementById("id_errorMessage").textContent = message;
                                break;
                            case 500:   //server error
                                document.getElementById("id_errorMessage").textContent = message;
                                break;
                        }
                    }
                }, false);
        } else if (firstPassword.localeCompare(secondPassword) !== 0)
            document.getElementById("id_errorMessage").textContent = "Passwords aren't equals, try again";
        else
            form.reportValidity();
    });
})();