(function () {


    let albumTable;

    // load event
    window.addEventListener("load", () => {
        pageOrchestrator = new PageOrchestrator();
        pageOrchestrator.start(); // inizializza i componenti
        pageOrchestrator.refresh(); // mostra i componenti
    }, false);


    // controller
    function PageOrchestrator() {

        let mainAlert = document.getElementById("id_mainAlert");
        let alertAlbumTable = document.getElementById("id_alert_albums");


        // FUNZIONI
        this.start = function () {

            let username = sessionStorage.getItem('username');
            let message = document.getElementById("id_username");

            let personalMessage = new PersonalMessage(username, message);

            personalMessage.show();


            albumTable = new AlbumTable(
                alertAlbumTable,
                document.getElementById("id_table_albums"),
                document.getElementById("id_tableBody_albums"));

        };


        this.refresh = function() {
            albumTable.reset();
        };
    }

    function PersonalMessage(_username, messagecontainer) {
        this.username = _username;
        this.show = function() {
            // stampa all'inizio della home il nome dell'utente
            messagecontainer.textContent = this.username;
        }
    }

    function AlbumTable(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;            // intera tabella html dei meetings
        this.listcontainerbody = _listcontainerbody;    // solo il body della tabella html dei meetings

        this.reset = function () {

            this.listcontainer.style.visibility = "hidden";

            let albumController = new AlbumController(this.alert);
            albumController.getAlbums();
        }

        // chiama la update() se meeting non è vuota, altrimenti stampa l'alert
        this.show = function (albums, autoclick) {

            const self = this;

            // se albums non è vuota...
            if (albums.length !== 0){

                self.update(albums);
                if (autoclick) self.autoclick(); // mostra il primo album della tabella

            }
            else this.alert.textContent = "No albums yet!";
        };

        // compila la tabella con i meetings che il server gli fornisce
        this.update = function (albums) {

            var l = albums.length;
            let row, titleCell, dateCell, linkcell, anchor;

            if (l === 0) {  // controllo inutile ma per sicurezza XD
                alert.textContent = "No albums yet!";

            } else {
                this.listcontainerbody.innerHTML = ""; // svuota il body della tabella
                this.alert.textContent = "";

                const self = this;

                albums.forEach(function (album) {

                    row = document.createElement("tr");

                    // prima cella della riga (titolo)
                    titleCell = document.createElement("td");
                    titleCell.textContent = album.title;
                    row.appendChild(titleCell);

                    // seconda cella della riga (data)
                    dateCell = document.createElement("td");
                    dateCell.textContent = album.date;
                    row.appendChild(dateCell);

                    // quarta cella della riga (ancora x dettagli)
                    linkcell = document.createElement("td");

                    anchor = document.createElement("a");
                    let linkText = document.createTextNode("Show");
                    anchor.appendChild(linkText);
                    anchor.setAttribute('albumId', album.id);    //IntelliJ lo dà non safe perché "idMeeting" è un attributo del database, preso da JSON. IntelliJ non ha idea che esista.

                    anchor.addEventListener("click", (e) => {

                        e.preventDefault();

                        if(selectedCell !== undefined) selectedCell.className = ""; // entra solo la prima volta

                        selectedCell = e.target.closest("td");
                        selectedCell.className = "detailSelected";  // colora di giallo la cella

                        meetingDetails.show(e.target.getAttribute("albumId")); // the list must know the details container
                    }, false);

                    anchor.href = "#";

                    linkcell.appendChild(anchor);

                    row.appendChild(linkcell);

                    self.listcontainerbody.appendChild(row);
                });
                this.listcontainer.style.visibility = "visible";
            }
        }

        this.autoclick = function (albumId) {

            var e = new Event("click"); // crea l'evento
            var selector = "a[albumId='" + albumId + "']";

            var anchorToClick = (albumId) // se meetingId != undefined..
                ? document.querySelector(selector)  // ..allora prendi l'ancora relariva al meetingId
                : this.listcontainerbody.querySelectorAll("a")[0];  // ..altrimenti prendi la prima ancora della tabella

            anchorToClick.dispatchEvent(e); // avvia l'evento
        }

    }

    function AlbumController(_alert) {
        this.alert = _alert;

        // richiede al server tutti i meeting, sia quelli che ho creato che quelli a cui partecipo
        this.getAlbums = function(currentAlbum) {

            var self = this;

            makeCall("GET", "GetAlbums", null,
                function (req) {
                    if (req.readyState === XMLHttpRequest.DONE) {

                        let message = req.responseText;

                        if (req.status === 200) {

                            let albums = JSON.parse(message);
                            albumTable.show(albums, function() {albumTable.autoclick(currentAlbum);});


                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        }
    }

})();