export const deleteRow = (indexName) => {
    document.getElementById('delete-row').innerHTML = `
        <div class="delete-row-box" id="delete-row-box">
            <h5>Delete new row</h5>
            <h5>${indexName}</h5>
            <input class="search-box" id="${indexName}-search"></input>
            <button class="${indexName}" id="${indexName}-del">Delete</button>
        </div>
    `
}

export const deleteRowRemove = () => {
    document.getElementById('delete-row').innerHTML = ``;
}

export const deleteRowFromDB = async (indexName) => {
    const value = document.getElementById(`${indexName}-search`).value;
    let url;
    if (indexName === 'account_number') {
        url = "http://localhost:7000/api/deleteAccount";
        if (value === '') {
            alert("account_number is null hence it cant be deleted");
            location.reload();
            return;
        }
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    account_number: value
                })
            });
            const json = await response.json();
            if ('error' in json) {
                alert(json['error']);
                location.reload();
                return;
            }
            alert(json['success']);
            location.reload();
        } catch (e) {
            console.error(e);
            alert(e);
        }
    } else if (indexName === 'account_type') {
        url = "http://localhost:7000/api/deleteAccountType";
        if (value === '') {
            alert("account_type is null hence it cant be deleted");
            location.reload();
            return;
        }
        try {
            const response =  await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    account_type: value
                })
            });
            const responseJson = await response.json();
            if ("error" in responseJson) {
                alert(responseJson['error']);
            } else {
                alert(responseJson["success"]);
            }
            location.reload();
        } catch (e) {
            console.error(e);
            alert(e);
        }
    }

}