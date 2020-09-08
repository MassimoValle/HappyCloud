(function () {


    let albumTable;
    let photoTable;
    let photoDetailsTable;
    let commentsTable;
    let commentForm;

    let currentSet = 1;
    let photos;
    let photoSelected;

    // load event
    window.addEventListener("load", () => {
        let pageOrchestrator = new PageOrchestrator();
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
        this.listcontainer = _listcontainer;            // intera tabella html degli album
        this.listcontainerbody = _listcontainerbody;    // solo il body della tabella html degli album

        this.reset = function () {

            this.listcontainerbody.innerHTML = "";
            this.listcontainer.style.visibility = "hidden";

            let albumController = new AlbumController(this.alert);
            albumController.getAlbums();
        }

        this.show = function (albums) {

            let self = this;

            self.update(albums);
        };

        // compila la tabella con gli album che il server gli fornisce
        this.update = function (albums) {

            let l = albums.length;
            let row, titleCell, dateCell, linkcell, anchor;

            if (l === 0) {  // controllo inutile ma per sicurezza XD
                alert.textContent = "No albums yet!";

            } else {
                this.listcontainerbody.innerHTML = ""; // svuota il body della tabella
                this.alert.textContent = "";

                let self = this;

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
                    anchor.setAttribute('albumId', album.id);

                    anchor.addEventListener("click", (e) => {

                        e.preventDefault();
                        currentSet = 1;
                        photoTable.init(e.target.getAttribute("albumId"));  // quando faccio click su un album

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

        this.show = function (photos) {

            let self = this;

            self.update(photos);

        };

        // compila la tabella con le foto che il server gli fornisce
        this.update = function (photos) {

            let l = photos.length;
            let row;

            if (l === 0) {  // controllo inutile ma per sicurezza XD
                alert.textContent = "No photos yet!";

            } else {
                this.listcontainerbody.innerHTML = ""; // svuota il body della tabella
                this.alert.textContent = "";

                let self = this;

                row = document.createElement("tr");

                if(currentSet > 1){

                    let backBtn = document.createElement("button");
                    backBtn.textContent = "<";
                    backBtn.addEventListener("click", (e) => {

                        e.preventDefault();
                        currentSet = currentSet-1;
                        self.show(photos);

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

                    anchor.addEventListener("mouseover", (e) => {

                        e.preventDefault();

                        let modal = document.getElementById("myModal");
                        modal.style.display = "block";

                        let arg = e.target.closest("a").getAttribute("photoId");
                        photoDetailsTable.show(arg);

                     }, false);

                    cell.appendChild(title);
                    cell.appendChild(anchor);

                    row.appendChild(cell);

                    index++;

                }

                if(photos.length > currentSet*5){

                    let nextBtn = document.createElement("button");
                    nextBtn.textContent = ">";
                    nextBtn.addEventListener("click", (e) => {

                        e.preventDefault();
                        currentSet = currentSet+1;
                        self.show(photos);

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

            let self = this;

            photoSelected = self.searchPhoto(photoId);

            if (photoSelected !== undefined){

                self.update(photoSelected);

                commentsTable.reset();  // devo farlo ogni volta per aggiornare la tabella
                commentsTable.show(photoSelected.comments);

            }
            else this.alert.textContent = "Problem on showing details";
        };


        this.searchPhoto = function (photoId) {

            let p;

            photos.forEach(function (photo) {
                if(photo.id == photoId){
                    p = photo;
                }
            });

            return p;
        }


        this.update = function (photo) {

            this.alert.textContent = "";

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

            let self = this;

            // se albums non è vuota...
            if (comments.length !== 0){

                self.update(comments);
                //commentForm.show();

            }
            else this.alert.textContent = "No comments yet!";
            commentForm.show();
        };

        // compila la tabella con i meetings che il server gli fornisce
        this.update = function (comments) {

            let l = comments.length;
            let row, commentCell;

            if (l === 0) {  // controllo inutile ma per sicurezza XD
                alert.textContent = "No comments yet!";

            } else {
                this.listcontainerbody.innerHTML = ""; // svuota il body della tabella
                this.alert.textContent = "";

                let self = this;

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

        this.show = function () {
            this.listcontainer.style.visibility = "visible";
        };

    }




    // CONTROLLERS
    function AlbumController(_alert) {
        this.alert = _alert;

        // richiede al server tutti i gli album
        this.getAlbums = function() {

            let self = this;

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

        // richiede al server tutte le foto
        this.getPhotos = function(albumId) {

            let self = this;

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



    // COMMENT FORM
    document.getElementById("commentButton").addEventListener('click', (e) => {
        e.preventDefault();

        let form = e.target.closest("form");

        // imposto l'immagine selezionata nel campo imgSelected della form
        document.getElementById("id_form_comment").elements["imgSelected"].value = photoSelected.id;


        if (form.checkValidity()) {
            makeCall("POST", 'AddComment', e.target.closest("form"),
                function(req) {
                    if (req.readyState === XMLHttpRequest.DONE) {

                        let message = req.responseText;     // risposta del server
                        switch (req.status) {
                            case 200:
                                form.reset();
                                let comments = JSON.parse(message);
                                photoSelected.comments = comments;
                                commentsTable.show(photoSelected.comments);

                                break;
                            case 400: // bad request
                                document.getElementById("id_alert_comments").textContent = message;
                                break;
                            case 401: // unauthorized
                                document.getElementById("id_alert_comments").textContent = message;
                                break;
                            case 500: // server error
                                document.getElementById("id_alert_comments").textContent = message;
                                break;
                        }
                    }
                }, false);
        } else {
            form.reportValidity();
        }
    });




    // MODAL WINDOW
    document.getElementsByClassName("close")[0].onclick = function() {
        let modal = document.getElementById("myModal");
        modal.style.display = "none";
    }

    // Quando clicki fuori dall finestra modale, questa si chiude
    window.onclick = function(event) {
        let modal = document.getElementById("myModal");
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
})();