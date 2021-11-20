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

function deleteEventPlan(obj, url) {
    fetch(url, {
        method : 'DELETE'})
        .then(response => {
            if(!response.ok) {
                console.log("Unable to delete event id " + url)
                alert('Unable to delete event plan at the moment')
            } else {
                alert('event plan deleted successfully!')
                location.reload()
            }
        }).catch((error) => {
            console.log("internal error while deleting event " + error)
    })
}
