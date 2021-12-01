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
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
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

$('#shopping-cart').on('click', '.remove-item', function () {
    console.log('i am gonna remove an item for ' + $(this).attr('id'));
    let itemName = $(this).attr('data-item-name');
    console.log('i am gonna remove an item ' + itemName);
    let url = '/view/cart/removeItem/' + itemName;
    fetch(url)
        .then(response => {
            if (response.ok) {
                $(this).closest('tr').remove();// remove the closest tr
                location.reload();
            } else {
                alert("unable to remove item at the moment!");
            }
        }).catch(error => {
        console.error("unable to remove item from cart " + error);
    })
});

$('#add-ing-cart').click(function () {
        console.log("adding to cart")
    }
)

$('#shopping-cart').on('click', '.increase-item', function () {
    console.log("increase item count ");
    let currentQuantity = $(this).closest('.quantity').find('input');
    let itemName = $(this).attr('data-item-name');
    console.log("current quantity " + currentQuantity.val() + ", for item " + itemName);
    let url = '/view/cart/increaseItem/' + itemName;
    fetch(url)
        .then(res => {
            if (res.ok) {

                console.log('item ' + itemName + ' increased successfully');
                currentQuantity.val(currentQuantity.val() + 1);
                location.reload();
            } else {
                alert('unable to increase qty at the moment!');
            }
        }).catch(err => console.error('unable to increase item ' + err))
});

$('#shopping-cart').on('click', '.reduce-item', function () {
    console.log("decreaes item count ");
    let currentQuantity = $(this).closest('.quantity').find('input');
    let itemName = $(this).attr('data-item-name');
    console.log("current quantity " + currentQuantity.val() + ", for item " + itemName);
    if (currentQuantity <= 0) {
        alert("you can't reduce further items!");
        return;
    }
    let url = '/view/cart/reduceItem/' + itemName;
    fetch(url)
        .then(res => {
            if (res.ok) {
                console.log('item ' + itemName + ' reduced successfully');
                currentQuantity.val(currentQuantity.val() - 1);
                location.reload();
            } else {
                alert('unable to decrease qty at the moment!');
            }
        }).catch(err => console.error('unable to decrease item ' + err))
});


function addToCart(obj, url) {
    console.log("adding to cart");
    fetch(url)
        .then(response => {
            if (!response.ok) {
                console.log("unable to add item to cart " + url);
                alert('Cannot add item to cart');
            } else {
                console.log("successfully added to cart !" + url);
                location.reload();
            }
        })
        .catch((error) => {
            console.log("error while adding item to cart " + error);
        })
}


$('#add-ing-btn').click(function () {
    console.log('add called on ' + $(this).attr('id'));
    // add new row
    var listItem = document.createElement("li");
    //label
    var label = document.createElement("label");//label
    //input (text)
    var editInput = document.createElement("input");//text
    //button.edit
    var editButton = document.createElement("button");//edit button

    //button.delete
    var deleteButton = document.createElement("button");//delete button

    label.innerText = $('#new-ingredient').val();
    editInput.value = $('#new-ingredient').val();
    //Each elements, needs appending
    editInput.type = "text";
    editInput.name = "ingredientsList";

    editButton.innerText = "Edit";//innerText encodes special characters, HTML does not.
    editButton.className = "edit";

    editButton.onclick = function () {
        if (listItem.classList.contains("editMode")) {
            label.innerText = editInput.value;
        } else {
            editInput.value = label.innerText;
        }
        listItem.classList.toggle("editMode");
    }
    deleteButton.innerText = "Delete";
    deleteButton.className = "delete";
    deleteButton.onclick = function () {
        listItem.remove();
    }

    //and appending.
    listItem.appendChild(label);
    listItem.appendChild(editInput);
    listItem.appendChild(editButton);
    listItem.appendChild(deleteButton);
    console.log('list item currently is ' + listItem);
    console.log('total recipes currently ' + $('.ing-list li').length)
    $('.ing-list').append(listItem);
})

