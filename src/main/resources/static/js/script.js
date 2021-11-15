function toggleFavorite(x, url) {
    fetch(url)
        .then(response => {
            if(!response.ok) {
                console.log("unsuccessful response while liking " + url);
            }
        }).catch((error) => console.log("error while liking " + error))
}