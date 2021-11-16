function toggleFavorite(x, url) {
    fetch(url)
        .then(response => {
            if (!response.ok) {
                console.log("unsuccessful response while liking " + url);
                alert('Unable to like / dislike!');
                x.checked = !x.checked;
            }
        })
        .catch((error) => {
            console.log("error while liking " + error);
            x.checked = !x.checked;
        })
}