(function(){
    document.getElementById("registrationButton").addEventListener("click", (event) => {
        event.preventDefault();

        const form = event.target.closest("form");




        if (form.checkValidity()) {

            const firstPassword = form.elements["password"].value;
            const secondPassword = form.elements["confPassword"].value;

            if(firstPassword.localeCompare(secondPassword) !== 0){
                document.getElementById("id_errorMessage").textContent = "Passwords aren't equals, try again";
                return;
            }



            var email = form.elements["email"].value;

            if(!validateEmail(email)){
                document.getElementById("id_errorMessage").textContent = "Invalid email format";
                return;
            }




            makeCall("POST", "Signup", form,
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
        } else
            form.reportValidity();
    });

    function validateEmail(email) {
        const re = /^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$/;
        return re.test(String(email).toLowerCase());
    }
})();