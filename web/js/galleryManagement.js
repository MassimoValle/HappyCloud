(function () {


    let albumTable;
    let photoTable;
    let photoDetailsTable;
    let commentsTable;
    let commentForm;

    let currentSet = 1;
    let photos;

    // load event
    window.addEventListener("load", () => {
        pageOrchestrator = new PageOrchestrator();
        pageOrchestrator.start(); // inizializza i componenti
        pageOrchestrator.refresh(); // mostra i componenti
    }, false);


    // controller
    function PageOrchestrator() {

        let mainAlert = document.getElementById("id_mainAlert");


        // FUNZIONI
        this.start = function () {

            let username = sessionStorage.getItem('username');
            let message = document.getElementById("id_username");

            let personalMessage = new PersonalMessage(username, message);

            personalMessage.show();


            albumTable = new AlbumTable(
                document.getElementById("id_alert_albums"),
                document.getElementById("id_table_albums"),
                document.getElementById("id_tableBody_albums"));

            photoTable = new PhotoTable(
                document.getElementById("id_alert_photos"),
                document.getElementById("id_table_photos"),
                document.getElementById("id_tableBody_photos"));

            photoDetailsTable = new PhotoDetailsTable(
                document.getElementById("id_alert_photoDetails"),
                document.getElementById("id_table_photoDetails"),
                document.getElementById("id_tableBody_photoDetails"));

            commentsTable = new CommentsTable(
                document.getElementById("id_alert_comments"),
                document.getElementById("id_table_comments"),
                document.getElementById("id_tableBody_comments"));

            commentForm = new CommentForm(
                document.getElementById("id_fieldset_comment"),
                document.getElementById("id_form_comment"));

        };


        this.refresh = function() {
            albumTable.reset();
            photoTable.reset();
            photoDetailsTable.reset();
            commentsTable.reset();
            commentForm.reset();

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

            this.listcontainerbody.innerHTML = "";
            this.listcontainer.style.visibility = "hidden";

            let albumController = new AlbumController(this.alert);
            albumController.getAlbums();
        }

        // chiama la update() se meeting non è vuota, altrimenti stampa l'alert
        this.show = function (albums) {

            const self = this;

            // se albums non è vuota...
            if (albums.length !== 0){
                self.update(albums);
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

                        photoTable.init(e.target.getAttribute("albumId"));

                    }, false);

                    anchor.href = "#";

                    linkcell.appendChild(anchor);

                    row.appendChild(linkcell);

                    self.listcontainerbody.appendChild(row);
                });
                this.listcontainer.style.visibility = "visible";
            }
        }

    }

    function PhotoTable(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;            // intera tabella html dei meetings
        this.listcontainerbody = _listcontainerbody;    // solo il body della tabella html dei meetings

        this.reset = function () {

            this.listcontainerbody.innerHTML = "";
            this.listcontainer.style.visibility = "hidden";
        }

        this.init = function (albumId) {

            let photoController = new PhotoController(this.alert);
            photoController.getPhotos(albumId);
        }

        // chiama la update() se meeting non è vuota, altrimenti stampa l'alert
        this.show = function (photos) {

            const self = this;

            // se albums non è vuota...
            if (photos.length !== 0){

                self.update(photos);

            }
            else this.alert.textContent = "No photos yet!";
        };

        // compila la tabella con i meetings che il server gli fornisce
        this.update = function (photos) {

            const l = photos.length;
            let row;

            if (l === 0) {  // controllo inutile ma per sicurezza XD
                alert.textContent = "No photos yet!";

            } else {
                this.listcontainerbody.innerHTML = ""; // svuota il body della tabella
                this.alert.textContent = "";

                const self = this;

                row = document.createElement("tr");

                if(currentSet > 1){

                    let backBtn = document.createElement("input");
                    backBtn.value = "<";
                    backBtn.addEventListener("click", (e) => {

                        e.preventDefault();
                            currentSet = currentSet-1;
                            self.update(photos);

                        }, false);

                    let cell = document.createElement("td");
                    cell.appendChild(backBtn);
                    row.appendChild(cell);
                }


                let index = (currentSet-1) * 5;

                for (let i = 0; i < 5 && index < l; i++) {

                    let photo = photos[index];

                    let cell = document.createElement("td");

                    let title = document.createElement("p");
                    title.textContent = photo.title;

                    let anchor = document.createElement("a");
                        let image = document.createElement("img");
                        image.className = "thumbnail";
                        image.src = photo.path;
                    anchor.appendChild(image);
                    anchor.setAttribute('photoId', photo.id);

                    anchor.addEventListener("click", (e) => {

                        e.preventDefault();

                        let img = e.target;
                        let a = img.parentElement;

                        let arg = a.getAttribute("photoId");

                        photoDetailsTable.show(arg);

                     }, false);

                    cell.appendChild(title);
                    cell.appendChild(anchor);

                    row.appendChild(cell);

                    index++;

                }

                if(photos.length > currentSet*5){

                    let nextBtn = document.createElement("input");
                    nextBtn.value = ">";
                    nextBtn.addEventListener("click", (e) => {

                        e.preventDefault();
                        currentSet = currentSet+1;
                        self.update(photos);

                    }, false);

                    let cell = document.createElement("td");
                    cell.appendChild(nextBtn);
                    row.appendChild(cell);

                }


                self.listcontainerbody.appendChild(row);
                this.listcontainer.style.visibility = "visible";
            }
        }

    }

    function PhotoDetailsTable(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;            // intera tabella html dei meetings
        this.listcontainerbody = _listcontainerbody;    // solo il body della tabella html dei meetings

        this.reset = function () {

            this.listcontainer.style.visibility = "hidden";
        }


        // chiama la update() se meeting non è vuota, altrimenti stampa l'alert
        this.show = function (photoId) {

            const self = this;

            let photoSelected;

            photos.forEach(function (photo) {
                if(photo.id == photoId){
                    photoSelected = photo;
                }
            });

            if (photoSelected !== undefined){

                self.update(photoSelected);
                
                commentsTable.reset();
                commentsTable.show(photoSelected.comments);

            }
            else this.alert.textContent = "Problem on showing details";
        };


        this.searchPhoto = function (photoId) {
            photos.forEach(function (photo) {
                if(photo.id == photoId){
                    return photo;
                }
            });
        }


        this.update = function (photo) {

            this.alert.textContent = "";

            const self = this;

            let idTitle = document.getElementById("id_title_photoDetails");
            let idDate = document.getElementById("id_date_photoDetails");
            let idDescription = document.getElementById("id_description_photoDetails");
            let idImage = document.getElementById("id_image_photoDetails");


            idTitle.textContent = photo.title;
            idDate.textContent = photo.date;
            idDescription.textContent = photo.description;
            idImage.src = photo.path;

            this.listcontainer.style.visibility = "visible";
        }

    }

    function CommentsTable(_alert, _listcontainer, _listcontainerbody) {
        this.alert = _alert;
        this.listcontainer = _listcontainer;            // intera tabella html dei meetings
        this.listcontainerbody = _listcontainerbody;    // solo il body della tabella html dei meetings

        this.reset = function () {

            this.listcontainerbody.innerHTML = "";
            this.listcontainer.style.visibility = "hidden";
        }

        // chiama la update() se meeting non è vuota, altrimenti stampa l'alert
        this.show = function (comments) {

            const self = this;

            // se albums non è vuota...
            if (comments.length !== 0){
                self.update(comments);
                commentForm.show();
            }
            else this.alert.textContent = "No comments yet!";
        };

        // compila la tabella con i meetings che il server gli fornisce
        this.update = function (comments) {

            var l = comments.length;
            let row, commentCell;

            if (l === 0) {  // controllo inutile ma per sicurezza XD
                alert.textContent = "No comments yet!";

            } else {
                this.listcontainerbody.innerHTML = ""; // svuota il body della tabella
                this.alert.textContent = "";

                const self = this;

                comments.forEach(function (comment) {

                    row = document.createElement("tr");

                    // prima cella della riga (titolo)
                    commentCell = document.createElement("td");
                    commentCell.textContent = comment.username + ": " + comment.text;
                    row.appendChild(commentCell);

                    self.listcontainerbody.appendChild(row);
                });
                this.listcontainer.style.visibility = "visible";
            }
        }

    }

    function CommentForm(_listcontainer, _listcontainerbody) {
        this.listcontainer = _listcontainer;
        this.listcontainerbody = _listcontainerbody;

        this.reset = function () {
            this.listcontainer.style.visibility = "hidden";
        }

        // chiama la update() se meeting non è vuota, altrimenti stampa l'alert
        this.show = function () {
            this.listcontainer.style.visibility = "visible";
        };

    }

    function AlbumController(_alert) {
        this.alert = _alert;

        // richiede al server tutti i meeting, sia quelli che ho creato che quelli a cui partecipo
        this.getAlbums = function() {

            var self = this;

            makeCall("GET", "GetAlbums", null,
                function (req) {
                    if (req.readyState === XMLHttpRequest.DONE) {

                        let message = req.responseText;

                        if (req.status === 200) {

                            let albums = JSON.parse(message);
                            albumTable.show(albums);


                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        }
    }

    function PhotoController(_alert) {
        this.alert = _alert;

        // richiede al server tutti i meeting, sia quelli che ho creato che quelli a cui partecipo
        this.getPhotos = function(albumId) {

            var self = this;

            makeCall("GET", "GetPhotos?albumId=" + albumId, null,
                function (req) {
                    if (req.readyState === XMLHttpRequest.DONE) {

                        let message = req.responseText;

                        if (req.status === 200) {

                            photos = JSON.parse(message);
                            photoTable.show(photos);


                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        }
    }

})();